/**
 * 
 */
package lense.compiler.phases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.TypeAlreadyDefinedException;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.LiteralIntervalNode;
import lense.compiler.ast.LambdaExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.ast.LiteralSequenceInstanceCreation;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.PreExpression;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.RangeNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.Field;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.Property;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeKind;
import lense.compiler.type.TypeMember;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Visibility;

public class SemanticVisitor extends AbstractScopedVisitor {

	// TODO remove method discovery, it is done in StructureVisitor
	private Map<String, Set<MethodSignature>> defined = new HashMap<String, Set<MethodSignature>>();
	private Map<String, Set<MethodSignature>> expected = new HashMap<String, Set<MethodSignature>>();

	private LenseTypeDefinition ANY;
	private LenseTypeDefinition VOID;
	// private LenseTypeDefinition NOTHING;
	private LenseTypeDefinition currentType;

	public SemanticVisitor(SemanticContext sc) {
		super(sc);
		ANY = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Any", 0).get();
		VOID = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Void", 0).get();
		// NOTHING = (LenseTypeDefinition)
		// sc.resolveTypeForName("lense.core.lang.Nothing", 0).get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {

		LenseTypeSystem typeSystem = LenseTypeSystem.getInstance();
		if (!expected.isEmpty()) {
			outter: for (Map.Entry<String, Set<MethodSignature>> entry : expected.entrySet()) {
				Set<MethodSignature> def = defined.get(entry.getKey());

				if (def == null || def.isEmpty()) {
					throw new CompilationError("Method '" + entry.getKey() + "' is not defined");
				}

				for (MethodSignature found : entry.getValue()) {
					for (MethodSignature expected : def) {
						if (typeSystem.isSignaturePromotableTo(found, expected)) {
							continue outter;
						}
					}
				}
				throw new CompilationError("Method '" + entry.getKey() + "' is not defined");
			}
		}

		if (currentType!= null){
			if (!currentType.hasConstructor()) {
				// if no constructor exists, add a default one
				currentType.addConstructor(new Constructor("constructor", Collections.emptyList(), false));
			}

			currentType.getConstructors().filter(m -> m.getName() == null)
			.sorted((a, b) -> a.getParameters().size() - b.getParameters().size()).forEach(c -> {

				c.setName("constructor");

			});

			if (!currentType.isAbstract() && !currentType.isNative()) {
				LinkedList<TypeDefinition> superTypes = new LinkedList<>(currentType.getInterfaces());
				if (currentType.getSuperDefinition() != null) {
					superTypes.addFirst(currentType.getSuperDefinition());
				}

				for (TypeDefinition st : superTypes) {
					for (TypeMember mb : st.getMembers()) {
						if (mb.isMethod()) {
							Method m = (Method) mb;
							if (m.isAbstract()) {
								Collection<Method> implemented = currentType.getMethodsByName(m.getName());
								if (!implemented.stream().anyMatch(i -> typeSystem.isMethodImplementedBy(m, i))) {
									throw new CompilationError(currentType.getSimpleName() + " is not abstract and method "
											+ m.toString() + " is not implemented");
								}
							}
						}
					}
				}
			}

		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext doVisitBeforeChildren(AstNode node) {
		if (node instanceof ConstructorDeclarationNode) {

			ConstructorDeclarationNode constructorDeclarationNode = (ConstructorDeclarationNode) node;

			// defaults
			if (constructorDeclarationNode.getVisibility() == null) {
				constructorDeclarationNode.setVisibility(Visibility.Private);
			}

			constructorDeclarationNode.setReturnType(new TypeNode(this.getSemanticContext().currentScope().getCurrentType()));

		} else if (node instanceof MethodDeclarationNode) {

			MethodDeclarationNode m = (MethodDeclarationNode) node;

			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(Visibility.Private);
			}

			// auto-abstract if interface
			if (this.getSemanticContext().currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}
		} else if (node instanceof AccessorNode) {

			AccessorNode m = (AccessorNode) node;
			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(m.getParent().getVisibility());
			}

			if (this.getSemanticContext().currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}

			if (m.getParent().isIndexed()) {
				for (AstNode n : ((IndexerPropertyDeclarationNode) m.getParent()).getIndexes().getChildren()) {
					FormalParameterNode var = (FormalParameterNode) n;
					TypeVariable type = var.getTypeNode().getTypeVariable();

					this.getSemanticContext().currentScope().defineVariable(var.getName(), type, node).setInitialized(true);
				}
			}

		} else if (node instanceof ModifierNode) {

			ModifierNode m = (ModifierNode) node;
			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(m.getParent().getVisibility());
			}
			if (this.getSemanticContext().currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}
			if (m.getParent().isIndexed()) {
				for (AstNode n : ((IndexerPropertyDeclarationNode) m.getParent()).getIndexes().getChildren()) {
					FormalParameterNode var = (FormalParameterNode) n;
					TypeVariable type = var.getTypeNode().getTypeVariable();

					this.getSemanticContext().currentScope().defineVariable(var.getName(), type, node).setInitialized(true);
				}
			}

			TypeVariable type = m.getParent().getType().getTypeVariable();
			if (type == null){
				type = m.getParent().getType().getTypeParameter();
			}

			this.getSemanticContext().currentScope()
			.defineVariable(m.getValueVariableName(), type , node)
			.setInitialized(true);

		} else if (node instanceof ClassTypeNode) {
			ClassTypeNode t = (ClassTypeNode) node;

			int genericParametersCount = t.getGenerics() == null ? 0 : t.getGenerics().getChildren().size();

			Optional<TypeDefinition> maybeMyType = this.getSemanticContext().resolveTypeForName(t.getName(),
					genericParametersCount);

			LenseTypeDefinition myType;
			if (maybeMyType.isPresent()) {
				myType = (LenseTypeDefinition) maybeMyType.get();
			} else {
				myType = new LenseTypeDefinition(t.getName(), t.getKind(), ANY);
				myType = (LenseTypeDefinition) this.getSemanticContext().registerType(myType, genericParametersCount);
			}

			myType.setKind(t.getKind());

			if (myType.getKind() == LenseUnitKind.Interface) {
				myType.setAbstract(true);
			}

			Visibility visibility = t.getVisibility();
			if (visibility == Visibility.Undefined) {
				myType.setVisibility(Visibility.Private);
			}
			myType.setVisibility(visibility);
			myType.setAbstract(t.isAbstract());
			myType.setNative(t.isNative());

			// TODO annotations

			List<VariableInfo> myGenericTypes = new ArrayList<>();
			if (t.getGenerics() != null) {

				for (AstNode n : t.getGenerics().getChildren()) {
					GenericTypeParameterNode g = (GenericTypeParameterNode) n;

					TypeNode tn = g.getTypeNode();
					FixedTypeVariable typeVar = new FixedTypeVariable(ANY);

					myGenericTypes.add(this.getSemanticContext().currentScope().defineTypeVariable(tn.getName(), typeVar, t));

					// already done in naming phase
					// myType.addGenericParameter(tn.getName(), typeVar);
				}

			}

			TypeNode superTypeNode = t.getSuperType();
			TypeDefinition superType = ANY;
			if (superTypeNode != null) {
				superType = this.getSemanticContext().typeForName(superTypeNode.getName(),
						superTypeNode.getTypeParametersCount());

				if (superType.isGeneric()) {

					for (AstNode n : superTypeNode.getChildren()) {
						if (n instanceof GenericTypeParameterNode) {

						} else {
							TypeNode tn = (TypeNode) n;
							TypeDefinition rawInterfaceType = this.getSemanticContext().typeForName(tn.getName(),
									tn.getTypeParametersCount());
							TypeDefinition interfaceType = rawInterfaceType;
							if (rawInterfaceType.isGeneric()) {
								IntervalTypeVariable[] parameters = new IntervalTypeVariable[tn.getChildren().size()];
								int index = 0;
								for (AstNode a : tn.getChildren()) {
									GenericTypeParameterNode g = (GenericTypeParameterNode) a;
									TypeNode tt = g.getTypeNode();
									for (int i = 0; i < myGenericTypes.size(); i++) {
										VariableInfo v = myGenericTypes.get(i);
										if (v.getName().equals(tt.getName())) {
											parameters[index] = new DeclaringTypeBoundedTypeVariable(myType, i,
													tt.getName(), g.getVariance()).toIntervalTypeVariable();
										}
									}
									index++;
								}

								interfaceType = LenseTypeSystem.specify(rawInterfaceType, parameters);

							}

							tn.setTypeVariable(new FixedTypeVariable(interfaceType));
							myType.addInterface(interfaceType);
						}

					}
				}

				if (superType.getKind() == LenseUnitKind.Interface) {
					throw new CompilationError(node, t.getName() + " cannot extend interface " + superType.getName()
					+ ". Did you meant to use 'implements' instead of 'extends' ?.");
				}

				superTypeNode.setTypeVariable(new FixedTypeVariable(superType));

			}

			if (superType.equals(myType)) {
				if (!myType.equals(ANY)) {
					throw new CompilationError(node, t.getName() + " cannot extend it self");
				}
			} else {
				myType.setSuperTypeDefinition(superType);
			}

			this.getSemanticContext().currentScope().defineVariable("this", new FixedTypeVariable(myType), node)
			.setInitialized(true);

			this.getSemanticContext().currentScope().defineVariable("super", new FixedTypeVariable(superType), node)
			.setInitialized(true);

			TreeTransverser.transverse(t, new StructureVisitor(myType, this.getSemanticContext()));

			t.setTypeDefinition(myType);

			currentType = myType;



			if (t.getInterfaces() != null) {
				for (AstNode n : t.getInterfaces().getChildren()) {
					generifyInterfaceType(myType, myGenericTypes, (TypeNode) n);

				}
			}

		} else if (node instanceof VariableReadNode) {
			VariableReadNode v = (VariableReadNode) node;
			VariableInfo variableInfo = this.getSemanticContext().currentScope().searchVariable(v.getName());
			if (variableInfo == null) {
				throw new CompilationError(node, "Variable " + v.getName() + " was not defined");
			}
			if (!variableInfo.isInitialized()) {

				throw new CompilationError(node, "Variable " + v.getName() + " was not initialized.");
			}
			v.setVariableInfo(variableInfo);

		} else if (node instanceof ForEachNode) {

			ForEachNode n = (ForEachNode) node;

			TreeTransverser.transverse(n.getContainer(), this);

			TypeVariable containerTypeVariable = n.getContainer().getTypeVariable();

			IntervalTypeVariable typeVariable = containerTypeVariable.getGenericParameters().get(0);

			n.getVariableDeclarationNode().setTypeNode(new TypeNode(typeVariable));

			this.getSemanticContext().currentScope().defineVariable(n.getVariableDeclarationNode().getName(), typeVariable, n)
			.setInitialized(true);

		} else if (node instanceof VariableWriteNode) {
			VariableWriteNode v = (VariableWriteNode) node;
			VariableInfo variableInfo = this.getSemanticContext().currentScope().searchVariable(v.getName());

			if (variableInfo == null) {
				throw new CompilationError("Variable " + v.getName() + " was not defined");
			} else if (variableInfo.getDeclaringNode() instanceof ClassTypeNode) {
				// a field is being set
				// TODO
			}

			variableInfo.markWrite();
			v.setVariableInfo(variableInfo);

		} else if (node instanceof LambdaExpressionNode) {

			LambdaExpressionNode n = ((LambdaExpressionNode) node);

			AstNode parent = n.getParent();
			while (!(parent instanceof ScopedVariableDefinitionNode)) {
				parent = n.getParent();
			}

			TypeVariable assignmentType = ((ScopedVariableDefinitionNode) parent).getTypeNode().getTypeVariable();

			List<IntervalTypeVariable> parameters = new ArrayList<>();
			if (assignmentType.getTypeDefinition().getName().equals("lense.core.lang.Function")) {
				parameters = ((FixedTypeVariable) assignmentType).getTypeDefinition().getGenericParameters();
			}

			int index = 1;
			for (AstNode p : n.getParameters().getChildren()) {

				FormalParameterNode d = ((FormalParameterNode) p);
				String name = d.getName();
				TypeVariable td = d.getTypeVariable();
				if (td == null) {
					td = parameters.get(index); // TODO Type inference to
					// nameresolution
					d.setTypeNode(new TypeNode(td));
				}

				this.getSemanticContext().currentScope().defineVariable(name, td, node).setInitialized(true);
				index++;
			}
		}

		return VisitorNext.Children;
	}

	private void generifyInterfaceType(LenseTypeDefinition parentType, List<VariableInfo> genericTypes, TypeNode tn) {

		TypeDefinition rawInterfaceType = this.getSemanticContext().typeForName(tn.getName(), tn.getTypeParametersCount());
		TypeDefinition interfaceType = rawInterfaceType;
		if (!rawInterfaceType.getGenericParameters().isEmpty()) {
			IntervalTypeVariable[] parameters = new IntervalTypeVariable[tn.getChildren().size()];
			int index = 0;
			for (AstNode a : tn.getChildren()) {
				GenericTypeParameterNode g = (GenericTypeParameterNode) a;
				TypeNode tt = g.getTypeNode();
				if (tt.getTypeVariable() == null || tt.getTypeVariable().getTypeDefinition().equals(ANY)) {
					for (int i = 0; i < genericTypes.size(); i++) {
						VariableInfo v = genericTypes.get(i);
						if (v.getName().equals(tt.getName())) {
							parameters[index] = new DeclaringTypeBoundedTypeVariable(parentType, i, tt.getName(),
									g.getVariance()).toIntervalTypeVariable();
						}
					}
				} else {
					if (tt.getTypeParametersCount() > 0) {
						// Recursive call
						generifyInterfaceType(null, genericTypes, tt);
						parameters[index] = new FixedTypeVariable(tt.getTypeVariable().getTypeDefinition())
								.toIntervalTypeVariable();
					} else {
						parameters[index] = tt.getTypeVariable().toIntervalTypeVariable();
					}
				}

				index++;
			}

			interfaceType = LenseTypeSystem.specify(rawInterfaceType, parameters);
			tn.setTypeVariable(new FixedTypeVariable(interfaceType));

			if (parentType != null) {
				parentType.addInterface(interfaceType);
				this.getSemanticContext().registerType(parentType, genericTypes.size());
			}

		} else {
			tn.setTypeVariable(new FixedTypeVariable(interfaceType));
			parentType.addInterface(interfaceType);
		}
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doVisitAfterChildren(AstNode node) {
		if (node instanceof ClassBodyNode){
			ClassBodyNode n = (ClassBodyNode)node;
			if (!currentType.hasConstructor() && currentType.getKind() == LenseUnitKind.Class) {
				// if no constructor exists, add a default one
				currentType.addConstructor(new Constructor("constructor", Collections.emptyList(), false));
				
				ConstructorDeclarationNode c = new ConstructorDeclarationNode();
				c.setReturnType(new TypeNode(currentType));
				c.setPrimary(true);
				c.setVisibility(Visibility.Public);
				n.add(c);
			}
			

		} else if (node instanceof TypeNode) {
			TypeNode t = (TypeNode) node;
			if (t.needsInference()) {
				return;
			}
			resolveTypeDefinition(t);

		} else if (node instanceof LiteralSequenceInstanceCreation) {
			LiteralSequenceInstanceCreation literal = (LiteralSequenceInstanceCreation) node;
			TypeDefinition maxType = ((TypedNode) literal.getArguments().getFirst().getFirstChild()).getTypeVariable()
					.getTypeDefinition();
			maxType = this.getSemanticContext().resolveTypeForName(maxType.getName(), maxType.getGenericParameters().size())
					.get();

			for (int i = 1; i < literal.getArguments().getChildren().size(); i++) {
				AstNode n = literal.getArguments().getChildren().get(i).getFirstChild();
				TypedNode t = (TypedNode) n;
				TypeDefinition type = t.getTypeVariable().getTypeDefinition();
				type = this.getSemanticContext().resolveTypeForName(type.getName(), type.getGenericParameters().size()).get();
				if (!type.equals(maxType)) {
					if (LenseTypeSystem.getInstance().isPromotableTo(maxType, type)) {
						maxType = type;
					} else if (!LenseTypeSystem.getInstance().isPromotableTo(type, maxType)) {
						// TODO incompatible types in the same array
						throw new CompilationError(node, "Heterogeneous Sequence");
					}
				}
			}

			ListIterator<AstNode> lstIterator = literal.getArguments().listIterator();

			FixedTypeVariable maxTypeDef = new FixedTypeVariable(maxType);
			while (lstIterator.hasNext()) {
				AstNode n = lstIterator.next().getFirstChild();
				TypeDefinition type = ((TypedNode) n).getTypeVariable().getTypeDefinition();
				if (!type.equals(maxType)) {
					if (LenseTypeSystem.getInstance().isPromotableTo(type, maxType)) {

						Optional<Constructor> op = maxType.getConstructorByParameters(new ConstructorParameter(type));

						lstIterator.set(NewInstanceCreationNode.of(maxTypeDef,  op.get(), n));

					}
				}
			}
			
			Optional<TypeDefinition> sequenceType = this.getSemanticContext().resolveTypeForName(LenseTypeSystem.Sequence().getName(), LenseTypeSystem.Sequence().getGenericParameters().size());

			literal.setTypeVariable(
					new FixedTypeVariable(LenseTypeSystem.specify(sequenceType.get(), maxType)));

		} else if (node instanceof LiteralAssociationInstanceCreation) {
			LiteralAssociationInstanceCreation literal = (LiteralAssociationInstanceCreation) node;

			TypeDefinition keypair = ((TypedNode) literal.getArguments().getFirst().getFirstChild()).getTypeVariable()
					.getTypeDefinition();
			IntervalTypeVariable keyType = keypair.getGenericParameters().get(0);
			IntervalTypeVariable valueType = keypair.getGenericParameters().get(1);

			// for( int i =1; i <literal.getArguments().getChildren().size();
			// i++){
			// AstNode n =
			// literal.getArguments().getChildren().get(i).getFirstChild();
			// TypedNode t = (TypedNode)n;
			// TypeDefinition type = t.getTypeVariable().getTypeDefinition();
			// type = this.getSemanticContext().resolveTypeForName(type.getName(),
			// type.getGenericParameters().size()).get();
			// if (!type.equals(maxType)){
			// if (LenseTypeSystem.getInstance().isPromotableTo(maxType, type)){
			// maxType = type;
			// } else if (!LenseTypeSystem.getInstance().isPromotableTo(type,
			// maxType)){
			// // TODO incompatible types in the same array
			// throw new CompilationError(node, "Heterogeneous Sequence");
			// }
			// }
			// }

			// ListIterator<AstNode> lstIterator =
			// literal.getArguments().listIterator();
			//
			// FixedTypeVariable maxTypeDef = new FixedTypeVariable(maxType);
			// while(lstIterator.hasNext()){
			// AstNode n = lstIterator.next().getFirstChild();
			// TypeDefinition type =
			// ((TypedNode)n).getTypeVariable().getTypeDefinition();
			// if (!type.equals(maxType)){
			// if (LenseTypeSystem.getInstance().isPromotableTo(type, maxType)){
			//
			// Optional<Constructor> op = maxType.getConstructorByParameters(new
			// ConstructorParameter(type));
			//
			//
			// final ClassInstanceCreationNode c = new
			// ClassInstanceCreationNode( maxTypeDef, n);
			// c.setConstructor(op.get());
			//
			// lstIterator.set(c);
			//
			//
			// }
			// }
			// }

			literal.setTypeVariable(
					new FixedTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Association(), keyType, valueType)));
		} else if (node instanceof LiteralTupleInstanceCreation) {
			LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);
			TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0).getFirstChild();
			TypedNode nextTuple;
			if (tuple.getChildren().get(1).getChildren().size() == 2) {
				nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1).getFirstChild();
			} else {
				nextTuple = new TypeNode(LenseTypeSystem.Nothing());
			}

			LenseTypeDefinition tupleType = LenseTypeSystem.specify(LenseTypeSystem.Tuple(), value.getTypeVariable(),
					nextTuple.getTypeVariable());

			//
			// TypeNode typeNode = new TypeNode(new
			// QualifiedNameNode(tuple.getTypeNode().getName()));
			// typeNode.addParametricType(new GenericTypeParameterNode(new
			// TypeNode(value.getTypeVariable())));
			// typeNode.addParametricType(new GenericTypeParameterNode(new
			// TypeNode(nextTuple.getTypeVariable())));
			//
			// typeNode.setTypeVariable();
			//
			// tuple.replace(tuple.getTypeNode(), typeNode);

			((LiteralTupleInstanceCreation) node).setTypeVariable(new FixedTypeVariable(tupleType));
		} else if (node instanceof RangeNode) {
			RangeNode r = (RangeNode) node;

			TypeVariable left = ((TypedNode) r.getChildren().get(0)).getTypeVariable();
			TypeVariable right = ((TypedNode) r.getChildren().get(1)).getTypeVariable();

			TypeVariable finalType;
			if (left.equals(right)) {
				finalType = left;
			} else if (LenseTypeSystem.getInstance().isPromotableTo(left, right)) {
				finalType = right;
			} else if (LenseTypeSystem.getInstance().isPromotableTo(right, left)) {
				finalType = left;
			} else {
				throw new CompilationError(node, "Cannot create range from " + left + " to " + right);
			}

			r.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Progression(), finalType)));
		} else if (node instanceof LiteralIntervalNode) {
			LiteralIntervalNode r = (LiteralIntervalNode) node;

			Optional<TypeVariable> oleft = Optional.ofNullable(r.getStart()).map(s -> s.getTypeVariable());
			Optional<TypeVariable> oright = Optional.ofNullable(r.getEnd()).map(s -> s.getTypeVariable());

			TypeVariable finalType = null;
			if (oleft.isPresent() && oright.isPresent()) {
				TypeVariable left = oleft.get();
				TypeVariable right = oright.get();

				TypeDefinition leftDef = this.getSemanticContext().resolveTypeForName(left.getTypeDefinition().getName(), left.getGenericParameters().size()).get();
				TypeDefinition rightDef = this.getSemanticContext().resolveTypeForName(right.getTypeDefinition().getName(), right.getGenericParameters().size()).get();
				
				if (left.equals(right)) {
					finalType = left;
				} else if (LenseTypeSystem.getInstance().isPromotableTo(leftDef, rightDef)) {
					finalType = right;
					
					// cast left to right
					
					Optional<Constructor> op = rightDef.getConstructorByParameters(new ConstructorParameter(left));
					
					NewInstanceCreationNode cast = NewInstanceCreationNode.of(finalType, op.get(), r.getStart());
					
					r.replace(r.getStart(), cast);
					
				} else if (LenseTypeSystem.getInstance().isPromotableTo(rightDef, leftDef)) {
					finalType = left;

					// cast right to left
					
					Optional<Constructor> op = leftDef.getConstructorByParameters(new ConstructorParameter(right));
					
					NewInstanceCreationNode cast = NewInstanceCreationNode.of(finalType, op.get(), r.getEnd());
					
					r.replace(r.getEnd(), cast);
				} else {
					throw new CompilationError(node, "Cannot create interval from " + left + " to " + right);
				}
			} else if (oleft.isPresent()) {
				finalType = oleft.get();
			} else if (oright.isPresent()) {
				finalType = oright.get();
			} else {
				throw new CompilationError(node, "Cannot create interval");
			}

			
			TypeDefinition type = this.getSemanticContext().resolveTypeForName("lense.core.math.Interval", 1).get();
			
			r.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(type, finalType)));
			
		
			
		} else if (node instanceof LambdaExpressionNode) {
			LambdaExpressionNode n = (LambdaExpressionNode) node;

			List<TypeVariable> generics = new ArrayList<>();

			generics.add(n.getBody().getTypeVariable());

			// TODO infer types
			for (AstNode v : n.getParameters().getChildren()) {
				FormalParameterNode vr = (FormalParameterNode) v;
				generics.add(vr.getTypeVariable());
			}

			TypeDefinition funtionType = LenseTypeSystem.specify(LenseTypeSystem.Function(generics.size()),
					generics.toArray(new TypeVariable[generics.size()]));

			n.setTypeVariable(new FixedTypeVariable(funtionType));


		} else if (node instanceof ArithmeticNode) {
			ArithmeticNode n = (ArithmeticNode) node;

			TypeVariable left = n.getLeft().getTypeVariable();
			TypeVariable right = n.getRight().getTypeVariable();

			if (left.equals(right)) {
				n.setTypeVariable(left);

				if (left.getTypeDefinition().equals(LenseTypeSystem.String())) {
					StringConcatenationNode c;
					if (n.getLeft() instanceof StringConcatenationNode) {
						c = (StringConcatenationNode) n.getLeft();
						c.add(n.getRight());
					} else {
						c = new StringConcatenationNode();
						c.add(n.getLeft());
						c.add(n.getRight());

					}
					c.setTypeVariable(left);
					n.getParent().replace(n, c);
				}
			} else {
				// find instance operator method
				TypeDefinition type = left.getTypeDefinition();

				if (type.equals(LenseTypeSystem.String())) {

					MethodInvocationNode convert = new MethodInvocationNode(n.getRight(), "asString");
					convert.setTypeVariable(left.toIntervalTypeVariable());

					StringConcatenationNode concat = new StringConcatenationNode();
					concat.add(n.getLeft());
					concat.add(convert);
					concat.setTypeVariable(left);

					n.getParent().replace(node, concat);
				} else {
					MethodSignature signature = new MethodSignature(n.getOperation().equivalentMethod(),
							new MethodParameter(right));

					Optional<Method> method = type.getMethodBySignature(signature);

					if (!method.isPresent()) {

						method = type.getMethodByPromotableSignature(signature);

						if (!method.isPresent()) {
							// search static operator
							throw new CompilationError(node, "Method " + n.getOperation().equivalentMethod() + "("
									+ right + ") is not defined in " + left);
						} else {
							// Promote
							Optional<Constructor> op = left.getTypeDefinition()
									.getConstructorByParameters(new ConstructorParameter(right));
							
						
							n.replace(n.getRight(), NewInstanceCreationNode.of(left,  op.get(), n.getRight()));
						}
					}

					
					ArgumentListItemNode arg = new ArgumentListItemNode(0, n.getRight());
					arg.setExpectedType(n.getRight().getTypeVariable());
				
					MethodInvocationNode invokeOp = new MethodInvocationNode(
							n.getLeft(),
							n.getOperation().equivalentMethod(),
							arg
					);

					List<CallableMemberMember<Method>> methodParameters = method.get().getParameters();
					if (methodParameters.size() != invokeOp.getCall().getArgumentListNode().getChildren().size()) {
						throw new CompilationError(node, "Argument count does not match parameters count");
					}

					for (int i = 0; i < methodParameters.size(); i++) {
						MethodParameter param = (MethodParameter) methodParameters.get(i);
						ArgumentListItemNode a = (ArgumentListItemNode) invokeOp.getCall().getArgumentListNode()
								.getChildren().get(i);
						a.setExpectedType(param.getType());
					}
					
					n.getParent().replace(node, invokeOp);

					invokeOp.setTypeVariable(method.get().getReturningType());

				}
			}

		} else if (node instanceof PosExpression) {
			PosExpression p = (PosExpression) node;

			String methodName;
			if (p.getOperation().equals(ArithmeticOperation.Decrement)) { /* a-- */
				methodName = "predecessor";
			} else if (p.getOperation().equals(ArithmeticOperation.Increment)) { /* a++ */
				methodName = "successor";
			} else {
				throw new CompilationError(node, "Unrecognized pos operator");
			}

			TypeVariable variable = ((TypedNode) p.getChildren().get(0)).getTypeVariable();

			TypeDefinition type = variable.getTypeDefinition();
			Optional<Method> list = type.getMethodsByName(methodName).stream()
					.filter(md -> md.getParameters().size() == 0).findAny();

			if (!list.isPresent()) {
				throw new CompilationError(node,
						"The method " + methodName + "() is undefined for TypeDefinition " + type);
			}

			// replace by a method invocation
			MethodInvocationNode method = new MethodInvocationNode(node.getChildren().get(0), methodName);
			method.setTypeVariable(list.get().getReturningType());

			if (node.getParent() instanceof ReturnNode) {
				node.getParent().replace(node, method);
			} else {

				VariableWriteNode left;
				if (node.getChildren().get(0) instanceof VariableReadNode) {
					left = new VariableWriteNode((VariableReadNode) node.getChildren().get(0));
				} else {
					throw new CompilationError(node, "Cannot call methodName at this point yet");
				}
				AssignmentNode assignment = new AssignmentNode(AssignmentNode.Operation.SimpleAssign);
				assignment.setLeft(left);
				assignment.setRight(method);

				node.getParent().replace(node, assignment);
			}

		} else if (node instanceof PreExpression) {
			PreExpression p = (PreExpression) node;

			final TypeDefinition type = ((TypedNode) p.getChildren().get(0)).getTypeVariable().getTypeDefinition();

			if (p.getOperation().equals(ArithmeticOperation.Subtraction)) { /* -a */
				String methodName = "symmetric";
				Optional<Method> list = type.getMethodsByName(methodName).stream()
						.filter(md -> md.getParameters().size() == 0).findAny();

				if (!list.isPresent()) {
					throw new CompilationError(node,
							"The method " + methodName + "() is undefined for TypeDefinition " + type);
				}

				// replace by a method invocation
				MethodInvocationNode method = new MethodInvocationNode(node.getChildren().get(0), methodName);

				method.setTypeVariable(list.get().getReturningType());

				node.getParent().replace(node, method);
			} else if (p.getOperation().equals(ArithmeticOperation.Addition)) { /* +a */
				// +a is a no-op. replace the node by its content
				node.getParent().replace(node, node.getChildren().get(0));
			} else if (p.getOperation().equals(ArithmeticOperation.Decrement)
					|| p.getOperation().equals(ArithmeticOperation.Increment)) {
				String methodName;
				if (p.getOperation().equals(ArithmeticOperation.Decrement)) { /* --a */
					methodName = "predecessor";
				} else if (p.getOperation().equals(ArithmeticOperation.Increment)) { /* ++a */
					methodName = "successor";
				} else {
					throw new CompilationError(node, "Unrecognized pos operator");
				}

				Optional<Method> list = type.getMethodsByName(methodName).stream()
						.filter(md -> md.getParameters().size() == 0).findAny();

				if (!list.isPresent()) {
					throw new CompilationError(node,
							"The method " + methodName + "() is undefined for TypeDefinition " + type);
				}

				// replace by a method invocation
				MethodInvocationNode method = new MethodInvocationNode(node.getChildren().get(0), methodName);
				method.setTypeVariable(list.get().getReturningType());

				if (node.getParent() instanceof ReturnNode) {
					node.getParent().replace(node, method);
				} else {

					VariableWriteNode left;
					if (node.getChildren().get(0) instanceof VariableReadNode) {
						VariableReadNode variableReadNode = (VariableReadNode) node.getChildren().get(0);
						left = new VariableWriteNode(variableReadNode);

					} else {
						throw new CompilationError(node, "Cannot call methodName at this point yet");
					}
					AssignmentNode assignment = new AssignmentNode(AssignmentNode.Operation.SimpleAssign);
					assignment.setLeft(left);
					assignment.setRight(method);

					node.getParent().replace(node, assignment);
				}

			} else {
				throw new CompilationError(node,
						"There is no unary " + p.getOperation().equivalentMethod() + " operation.");
			}
		} else if (node instanceof LiteralExpressionNode) {
			LiteralExpressionNode n = (LiteralExpressionNode) node;

			n.setTypeVariable(new FixedTypeVariable(
					this.getSemanticContext().resolveTypeForName(n.getTypeVariable().getTypeDefinition().getName(), 0).get()));

		} else if (node instanceof PreBooleanUnaryExpression) {
			PreBooleanUnaryExpression p = (PreBooleanUnaryExpression) node;

			final TypeDefinition type = ((TypedNode) p.getChildren().get(0)).getTypeVariable().getTypeDefinition();

			String methodName;
			if (p.getOperation().equals(BooleanOperation.BitNegate)) { /* ~a */
				// TODO verify operator interface Binary
				if (LenseTypeSystem.Boolean().equals(type)) {
					methodName = "flipAll";
				} else {
					throw new CompilationError(node,
							"Operator ~ can only be applied to Boolean instances ( found " + type.getName() + ")");
				}
			} else if (p.getOperation().equals(BooleanOperation.LogicNegate)) { /* !a */
				if (LenseTypeSystem.Boolean().equals(type)) {
					methodName = "negate";
				} else {
					throw new CompilationError(node,
							"Operator ! can only be applied to Boolean instances ( found " + type.getName() + ")");
				}
			} else {
				throw new CompilationError(node, "Unrecognized operator");
			}

			Optional<Method> list = type.getMethodsByName(methodName).stream()
					.filter(md -> md.getParameters().size() == 0).findAny();

			if (!list.isPresent()) {
				throw new CompilationError(node,
						"The method " + methodName + "() is undefined for TypeDefinition " + type);
			}

			// replace by a method invocation
			MethodInvocationNode method = new MethodInvocationNode(node.getChildren().get(0), methodName);

			method.setTypeVariable(list.get().getReturningType());

			node.getParent().replace(node, method);

		} else if (node instanceof AssignmentNode) {
			AssignmentNode n = (AssignmentNode) node;

			TypeVariable left = n.getLeft().getTypeVariable();
			TypeVariable right = n.getRight().getTypeVariable();

			if (!LenseTypeSystem.isAssignableTo(right, left)) {

				if (!LenseTypeSystem.getInstance().isPromotableTo(right, left)) {
					if ( left.getTypeDefinition().getName().equals(LenseTypeSystem.Maybe().getName())){
						// promotable to maybe
						if (!LenseTypeSystem.getInstance().isPromotableTo(right, left.getTypeDefinition().getGenericParameters().get(0))){
							throw new CompilationError(node,right + " is not assignable to " + left);
						}
						
						TypeDefinition someTpe = this.getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1).get();
						Optional<Constructor> op = someTpe.getConstructorByPromotableParameters(new ConstructorParameter(right));
						
						n.replace((AstNode) n.getRight(), NewInstanceCreationNode.of(new FixedTypeVariable(someTpe), op.get(), n.getRight()));
					} else {
						throw new CompilationError(node,right + " is not assignable to " + left);
					}

				} else   {
					// TODO change to promote node, promotion is implicit
					// constructor based
					Optional<Constructor> op = left.getTypeDefinition()
							.getConstructorByParameters(new ConstructorParameter(right));

					n.replace((AstNode) n.getRight(), NewInstanceCreationNode.of(left, op.get(), n.getRight()));
				}
			}

			if (n.getLeft() instanceof VariableWriteNode) {
				VariableInfo info = this.getSemanticContext().currentScope()
						.searchVariable(((VariableWriteNode) n.getLeft()).getName());

				if (info.isImutable() && info.isInitialized()) {
					throw new CompilationError(node,
							"Cannot modify the value of an imutable variable or field (" + info.getName() + ")");
				}
				info.setInitialized(true);
			} else if (n.getLeft() instanceof FieldOrPropertyAccessNode) {

				FieldOrPropertyAccessNode fp = (FieldOrPropertyAccessNode) n.getLeft();

				if (fp.getKind() == FieldKind.FIELD) {
					VariableInfo info = this.getSemanticContext().currentScope()
							.searchVariable(((FieldOrPropertyAccessNode) n.getLeft()).getName());

					if (info.isImutable() && info.isInitialized()) {

						AstNode parent = ((LenseAstNode) n.getLeft()).getParent().getParent().getParent();
						if (!(parent instanceof ConstructorDeclarationNode)) {
							throw new CompilationError(node,
									"Cannot modify the value of an imutable variable or field (" + info.getName()
									+ ")");
						}

					}
					info.setInitialized(true);
				}

			}
		} else if (node instanceof TernaryConditionalExpressionNode) {
			TernaryConditionalExpressionNode ternary = (TernaryConditionalExpressionNode) node;

			TypeVariable type = LenseTypeSystem.getInstance().unionOf(ternary.getThenExpression().getTypeVariable(),
					ternary.getElseExpression().getTypeVariable());

			if (type instanceof UnionType) {
				UnionType unionType = (UnionType) type;

				if (LenseTypeSystem.isAssignableTo(unionType.getLeft(), unionType.getRight())) {
					type = unionType.getRight(); // TODO promote side
				} else if (LenseTypeSystem.isAssignableTo(unionType.getRight(), unionType.getLeft())) {
					type = unionType.getLeft(); // TODO promote side
				} else if (LenseTypeSystem.getInstance().isPromotableTo(unionType.getLeft(), unionType.getRight())) {
					type = unionType.getRight(); // TODO promote side
				} else if (LenseTypeSystem.getInstance().isPromotableTo(unionType.getRight(), unionType.getLeft())) {
					type = unionType.getLeft(); // TODO promote side
				}

			}
			ternary.setTypeVariable(type);
		} else if (node instanceof FormalParameterNode) {
			FormalParameterNode formal = ((FormalParameterNode) node);

			try {
				this.getSemanticContext().currentScope().defineVariable(formal.getName(), formal.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {
				e.printStackTrace();
			}
		} else if (node instanceof ScopedVariableDefinitionNode) {
			ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode) node;

			TypedNode init = variableDeclaration.getInitializer();
			
			TypeVariable type = variableDeclaration.getTypeVariable();

			if (variableDeclaration.getTypeNode().needsInference()){
				if (init != null){
					type = init.getTypeVariable();
					variableDeclaration.getTypeNode().setTypeVariable(type);
				} else {
					throw new CompilationError(node, "Variable Type cannot be infered");
				}
			}
			
			VariableInfo info = this.getSemanticContext().currentScope().searchVariable(variableDeclaration.getName());

			if (info == null) {
				try {
					info = this.getSemanticContext().currentScope().defineVariable(variableDeclaration.getName(), type, node);
				} catch (TypeAlreadyDefinedException e) {
					if (!(node.getParent() instanceof ForEachNode)) {
						throw new CompilationError(node, e.getMessage());
					}
				}
			}

			info.setImutable(variableDeclaration.getImutabilityValue() == Imutability.Imutable);

			variableDeclaration.setInfo(info);

			

			if (init != null) {

				info.setInitialized(true);
				TypeVariable right = init.getTypeVariable();

				
				if (!LenseTypeSystem.isAssignableTo(right, type)) {
					if (LenseTypeSystem.getInstance().isPromotableTo(right, type)) {
						// TODO use promote node
						Optional<Constructor> op = type.getTypeDefinition()
								.getConstructorByParameters(new ConstructorParameter(right));

						variableDeclaration.setInitializer(NewInstanceCreationNode.of(type, op.get(), variableDeclaration.getInitializer()));
					} else if (LenseTypeSystem.getInstance().isTuple(type, 1)) { // TODO
						// better
						// polimorphism
						// for
						// promotion

						final LiteralTupleInstanceCreation m = new LiteralTupleInstanceCreation(
								variableDeclaration.getInitializer());

						variableDeclaration.setInitializer(m);

					} else {
						throw new CompilationError(node,
								right + " is not assignable to variable '" + info.getName() + "' of type " + type);
					}
				}
			}

			if (node instanceof FieldDeclarationNode) {
				FieldDeclarationNode f = (FieldDeclarationNode) node;

				LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope().getCurrentType();

				currentType.addField(f.getName(), f.getTypeVariable(), f.getImutabilityValue());

				// TODO only is used in constructor
				info.setInitialized(true);
			}
		} else if (node instanceof PropertyDeclarationNode) {
			PropertyDeclarationNode p = (PropertyDeclarationNode) node;

			if (p.getAcessor() != null && p.getModifier() != null
					&& p.getAcessor().isImplicit() ^ p.getModifier().isImplicit()) {
				throw new CompilationError(p, "Implicit properties cannot have implementation");
			}

			if (p.getInitializer() != null) {
				ExpressionNode exp = p.getInitializer();

				TypeVariable expType = exp.getTypeVariable();

				TypeVariable propType = p.getType().getTypeVariable();
				if (!LenseTypeSystem.isAssignableTo(expType, propType)) {
					if (!LenseTypeSystem.getInstance().isPromotableTo(expType, propType)) {
						throw new CompilationError(node,
								expType + " is not assignable to " + propType + " in property " + p.getName());
					} else {
						Optional<Constructor> op = propType.getTypeDefinition()
								.getConstructorByParameters(new ConstructorParameter(expType));

						p.replace(exp,  NewInstanceCreationNode.of(propType, op.get(), exp));
					}
				}
			}

			// auto-abstract if interface
			if (this.getSemanticContext().currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				p.setAbstract(true);

				if (p.getVisibility() == null) {
					p.setVisibility(Visibility.Public);
				}

				if (p.getVisibility() != Visibility.Public) {
					throw new CompilationError(node, "Members of an interface must be public");
				}

				if (p.getAcessor() != null) {
					p.getAcessor().setAbstract(true);
					p.getAcessor().setVisibility(p.getVisibility());
				}
				if (p.getModifier() != null) {
					p.getModifier().setAbstract(true);
					p.getModifier().setVisibility(p.getVisibility());
				}
			}

			LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope().getCurrentType();

			String typeName = p.getType().getName();
			VariableInfo genericParameter = this.getSemanticContext().currentScope().searchVariable(typeName);

			if (genericParameter != null && genericParameter.isTypeVariable()) {
				List<IntervalTypeVariable> parameters = currentType.getGenericParameters();
				Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);

				if (!opIndex.isPresent()) {
					throw new CompilationError(node,
							typeName + " is not a valid generic parameter for type " + currentType.getName());
				}

				int index = opIndex.get();

				DeclaringTypeBoundedTypeVariable pp = new DeclaringTypeBoundedTypeVariable(currentType, index, typeName,
						parameters.get(index).getVariance());

				if (p.isIndexed()) {

					lense.compiler.type.variable.TypeVariable[] params = new lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode) p)
					                                                                                                   .getIndexes().getChildren().size()];
					int i = 0;
					for (AstNode n : ((IndexerPropertyDeclarationNode) p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						TypeVariable type = var.getTypeNode().getTypeVariable();
						params[i++] = type;
						// this.getSemanticContext().currentScope().defineTypeVariable(var.getName(),
						// type, p).setInitialized(true);
					}

					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}

			} else {
				Optional<TypeDefinition> type = this.getSemanticContext().resolveTypeForName(p.getType().getName(),
						p.getType().getTypeParametersCount());
				p.getType().setTypeVariable(type.get());
				if (p.isIndexed()) {
					lense.compiler.type.variable.TypeVariable[] params = new lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode) p)
					                                                                                                   .getIndexes().getChildren().size()];
					int i = 0;
					for (AstNode n : ((IndexerPropertyDeclarationNode) p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						TypeVariable t = var.getTypeNode().getTypeVariable();
						params[i++] = t;
						// this.getSemanticContext().currentScope().defineTypeVariable(var.getName(),
						// t, p).setInitialized(true);
					}

					currentType.addIndexer(p.getType().getTypeVariable(), p.getAcessor() != null,
							p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), p.getType().getTypeVariable(), p.getAcessor() != null,
							p.getModifier() != null);
				}
			}

		} else if (node instanceof IndexedAccessNode) {
			IndexedAccessNode m = (IndexedAccessNode) node;

			TypedNode a = (TypedNode) m.getAccess();

			TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();

			TypeDefinition methodOwnerType = currentType;
			if (a != null) {
				if (a.getTypeVariable() instanceof FixedTypeVariable) {
					methodOwnerType = a.getTypeVariable().getTypeDefinition();
				} else {
					throw new UnsupportedOperationException();
				}

			}

			if (methodOwnerType.getName().equals("lense.core.collections.Tuple")) {
				// only one index is allowed
				if (m.getArguments().getChildren().size() != 1){
					throw new CompilationError(node, "Tuples only accept one index");
				}

				ExpressionNode indexArgument = (ExpressionNode)m.getArguments().getFirst().getFirstChild();
				Optional<Integer> index = asConstantNumber(indexArgument);
				if (index.isPresent()) {

					Optional<Method> tail = methodOwnerType.getMethodsByName("tail").stream()
							.filter(md -> md.getParameters().size() == 0).findAny();

					Optional<Method> head = methodOwnerType.getMethodsByName("head").stream()
							.filter(md -> md.getParameters().size() == 0).findAny();

					int max = countTupleSize(methodOwnerType);

					if (index.get().intValue() == 0) {
						MethodInvocationNode invoke = new MethodInvocationNode(m.getAccess(), "head");

						node.getParent().replace(node, invoke);
						invoke.setTypeVariable(methodOwnerType.getGenericParameters().get(0));
						return;
					} else if (index.get() < max) {
						MethodInvocationNode previous = new MethodInvocationNode(m.getAccess(), "tail");
						previous.setTypeVariable(methodOwnerType.getGenericParameters().get(1));

						for (int i = 0; i < index.get() - 1; i++) {
							MethodInvocationNode current = new MethodInvocationNode(previous, "tail");
							current.setTypeVariable(
									previous.getTypeVariable().getGenericParameters().get(1).getUpperbound());
							previous = current;
						}

						TypeVariable upperbound = previous.getTypeVariable().getGenericParameters().get(0)
								.getUpperbound();

						MethodInvocationNode invoke = new MethodInvocationNode(previous, "head");
						invoke.setTypeVariable(upperbound);

						CastNode cast = new CastNode(invoke, upperbound.getTypeDefinition());

						node.getParent().replace(node, cast);

						return;
					}

				}
			} else {

				TypeVariable[] signatureTypes = new TypeVariable[ m.getArguments().getChildren().size()];

				int index =0;
				for (AstNode n : m.getArguments().getChildren()){
					ArgumentListItemNode arg = (ArgumentListItemNode)n;
					TypeVariable type = arg.getExpectedType();

					if (type == null){
						type = ((TypedNode)arg.getFirstChild()).getTypeVariable();
						arg.setExpectedType(type);
					}
					signatureTypes[index++] = type ;
				}


				Optional<IndexerProperty> indexer = methodOwnerType.getIndexerPropertyByTypeArray(signatureTypes);

				if (!indexer.isPresent()) {
					throw new CompilationError(node, "No indexer " + signatureTypes
							+ " is defined for type " + methodOwnerType);
				}

				m.setTypeVariable(indexer.get().getReturningType());
			}



		} else if (node instanceof PosExpression) {
			PosExpression n = (PosExpression) node;
			n.setTypeVariable(((TypedNode) n.getChildren().get(0)).getTypeVariable());
		} else if (node instanceof FieldOrPropertyAccessNode) {
			FieldOrPropertyAccessNode m = (FieldOrPropertyAccessNode) node;

			VariableInfo info = this.getSemanticContext().currentScope().searchVariable("this");
			TypeVariable currentType = info.getTypeVariable();

			TypeVariable fieldOwnerType = currentType;

			String name = m.getName();

			AstNode access = m.getPrimary();

			if (access == null && name.contains(".")) {
				access = new QualifiedNameNode(name);
			}

			if (access == null) {
				// ok, analise after
			} else if (access instanceof QualifiedNameNode) {
				QualifiedNameNode qn = ((QualifiedNameNode) access);

				Optional<TypeDefinition> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = this.getSemanticContext().resolveTypeForName((qn).getName(), 0);
					} else {
						break;
					}
				}

				if (maybeType.isPresent()) {
					TypeDefinition def = maybeType.get();

					fieldOwnerType = new FixedTypeVariable(def);
					qn = ((QualifiedNameNode) access);

					Deque<String> path = new LinkedList<>();
					while (qn.getPrevious() != null) {
						path.add(qn.getLast().getName());
						qn = qn.getPrevious();

					}

					while (!path.isEmpty()) {
						String fieldName = path.pop();
						Optional<Field> maybeField = def.getFieldByName(fieldName);

						if (!maybeField.isPresent()) {

							Optional<Property> props = def.getPropertyByName(fieldName);

							if (!props.isPresent()) {
								throw new CompilationError(
										name + " is not a field or a property of TypeDefinition " + fieldOwnerType);
							} else {
								Property property = props.get();
								m.setTypeVariable(property.getReturningType());

								// Replace PropertyAccess

								return;
							}
						} else {
							Field field = maybeField.get();
							m.setTypeVariable(field.getReturningType());

							// Replace FieldAccess

							return;
						}
					}

				} else {
					// try variable
					VariableInfo variable = this.getSemanticContext().currentScope()
							.searchVariable(((QualifiedNameNode) access).getName());

					if (variable == null) {
						throw new CompilationError(((QualifiedNameNode) access).getName() + " variable is not defined");
					}

					// Replace Variable Read
					VariableReadNode read = new VariableReadNode(((QualifiedNameNode) access).getName());
					read.setVariableInfo(variable);

					m.replace(access, read);
					fieldOwnerType = variable.getTypeVariable();

				}
			} else if (access instanceof TypedNode) {
				fieldOwnerType = ((TypedNode) access).getTypeVariable();
			} else if (access instanceof CastNode) {
				fieldOwnerType = ((CastNode) access).getTypeVariable();
			} else if (access instanceof IdentifierNode) {

				VariableInfo variable = this.getSemanticContext().currentScope()
						.searchVariable(((IdentifierNode) access).getId());

				fieldOwnerType = variable.getTypeVariable();
			} else {
				throw new CompilationError(access.getClass() + " Not supported yet");
			}

			if (fieldOwnerType.equals(currentType)) {

				resolveFieldPropertyOrVariableName(node, m, currentType, fieldOwnerType, name);

			} else {

				if (!(fieldOwnerType instanceof FixedTypeVariable)) {
					throw new UnsupportedOperationException();
				}
				TypeDefinition def = ((FixedTypeVariable) fieldOwnerType).getTypeDefinition();
				Optional<Field> field = def.getFieldByName(name);

				if (!field.isPresent()) {

					Optional<Property> property = def.getPropertyByName(name);

					if (!property.isPresent()) {
						if (!LenseTypeSystem.isAssignableTo(def, LenseTypeSystem.Maybe())) {
							throw new CompilationError(node,
									"No field or property '" + name + "' is defined in " + fieldOwnerType);
						}

						TypeDefinition innerType = ((FixedTypeVariable) def.getGenericParameters().get(0))
								.getTypeDefinition();

						field = innerType.getFieldByName(name);

						if (!field.isPresent()) {
							throw new CompilationError(node,
									"No field or property " + name + " is defined in " + fieldOwnerType);
						}

						// transform to call inside the maybe using map
						TypeVariable finalType = new FixedTypeVariable(
								LenseTypeSystem.specify(LenseTypeSystem.Maybe(), field.get().getReturningType()));
						TypeDefinition mappingFunction = LenseTypeSystem.specify(LenseTypeSystem.Function(2),
								new FixedTypeVariable(innerType), field.get().getReturningType());

						NewInstanceCreationNode newObject = NewInstanceCreationNode.of(new FixedTypeVariable(mappingFunction));
						
						ArgumentListItemNode arg = new ArgumentListItemNode(0, newObject);
						arg.setExpectedType(newObject.getTypeVariable());
					
						
						MethodInvocationNode transform = new MethodInvocationNode(
								m.getPrimary(), 
								"map",
								arg
								 // TODO
								// lambda
								);

						m.getParent().replace(m, transform); // this operation
						// will
						// nullify the
						// transform.type.
						m.setTypeVariable(finalType); // set it again
						transform.setTypeVariable(finalType); // set it again
					} else {
						m.setTypeVariable(property.get().getReturningType());
						m.setKind(FieldOrPropertyAccessNode.FieldKind.PROPERTY);
					}
				} else {
					m.setTypeVariable(field.get().getReturningType());
					m.setKind(FieldOrPropertyAccessNode.FieldKind.FIELD);
				}
			}
		} else if (node instanceof ArgumentListNode) {
			ArgumentListNode m = (ArgumentListNode) node;

			ListIterator<AstNode> it = m.listIterator();

			while (it.hasNext()) {
				AstNode a = it.next().getFirstChild();
				if (a instanceof IdentifierNode) {
					IdentifierNode id = (IdentifierNode) a;
					VariableInfo info = this.getSemanticContext().currentScope().searchVariable(id.getId());

					if (info == null) {
						// try field

						TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();

						Optional<Field> field = currentType.getFieldByName(id.getId());

						if (!field.isPresent()) {

							Optional<Property> property = currentType.getPropertyByName(id.getId());

							if (!property.isPresent()) {
								throw new CompilationError(id, id.getId() + " is not a variable or a field");
							} else {
								FieldOrPropertyAccessNode r = new FieldOrPropertyAccessNode(id.getId());
								r.setKind(FieldKind.PROPERTY);
								r.setType(property.get().getReturningType());
								it.set(new ArgumentListItemNode(it.nextIndex() - 1, r));
							}

						} else {
							FieldOrPropertyAccessNode r = new FieldOrPropertyAccessNode(id.getId());
							r.setKind(FieldKind.FIELD);
							r.setType(field.get().getReturningType());
							it.set(new ArgumentListItemNode(it.nextIndex() - 1, r));
						}

					} else {
						VariableReadNode r = new VariableReadNode(id.getId(), info);
						it.set(new ArgumentListItemNode(it.nextIndex() - 1, r));
					}
				} else {
					continue;
				}
			}

		} else if (node instanceof MethodInvocationNode) {
			MethodInvocationNode m = (MethodInvocationNode) node;

			if (m.getTypeVariable() != null) {
				return;
			}

			TypeVariable methodOwnerType = this.getSemanticContext().currentScope().searchVariable("this").getTypeVariable();
			TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();

			String name = m.getCall().getName();

			AstNode access = m.getAccess();

			if (access == null) {
				// access to self
				MethodParameter[] parameters = asMethodParameters(m.getCall().getArgumentListNode());
				MethodSignature signature = new MethodSignature(name, parameters);

				Optional<Method> method = currentType.getMethodBySignature(signature);

				if (!method.isPresent()) {

					method = currentType.getMethodByPromotableSignature(signature);

					if (!method.isPresent()) {
						throw new CompilationError(node, "Method " + signature + " is not defined in " + methodOwnerType
								+ " or its super classes");
					}
				}

				m.setTypeVariable(method.get().getReturningType());

			} else if (access instanceof QualifiedNameNode) {
				QualifiedNameNode qn = ((QualifiedNameNode) access);

				Optional<TypeDefinition> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = this.getSemanticContext().resolveTypeForName((qn).getName(), 0);
					} else {
						break;
					}
				}

				if (maybeType.isPresent()) {
					TypeDefinition def = maybeType.get();
					methodOwnerType = new FixedTypeVariable(def);

					qn = ((QualifiedNameNode) access);

					Deque<String> path = new LinkedList<>();
					while (qn.getPrevious() != null) {
						path.add(qn.getLast().getName());
						qn = qn.getPrevious();

					}

					while (!path.isEmpty()) {
						String fieldName = path.pop();
						Optional<Field> maybeField = def.getFieldByName(fieldName);

						if (!maybeField.isPresent()) {

							throw new CompilationError(
									"The field " + name + " is undefined for TypeDefinition " + methodOwnerType);
						} else {
							Field field = maybeField.get();
							methodOwnerType = field.getReturningType();
						}
					}

					if (def.getKind() == LenseUnitKind.Object) {
						ObjectReadNode vnode = new ObjectReadNode(def, (qn).getName());

						access.getParent().replace(access, vnode);

						methodOwnerType = vnode.getTypeVariable();
					}

				} else {
					// try variable
					String varName = ((QualifiedNameNode) access).getName();
					VariableInfo variableInfo = this.getSemanticContext().currentScope().searchVariable(varName);

					if (variableInfo == null) {

						Optional<TypeDefinition> obj = this.getSemanticContext().resolveTypeForName(varName, 0);

						if (obj.isPresent() && obj.get().getKind() == LenseUnitKind.Object) {
							ObjectReadNode vnode = new ObjectReadNode(obj.get(), varName);

							access.getParent().replace(access, vnode);

							methodOwnerType = vnode.getTypeVariable();
						}

						// try property
						Optional<Property> property = this.getSemanticContext().currentScope().getCurrentType()
								.getPropertyByName(varName);

						if (!property.isPresent()) {

							// try a super property
							property = this.getSemanticContext().currentScope().getCurrentType().getSuperDefinition()
									.getPropertyByName(varName);

							if (!property.isPresent()) {
								throw new CompilationError(((QualifiedNameNode) access).getName()
										+ " is not a valid field, property or object");
							}

						}

						if (property.isPresent()) {

							FieldOrPropertyAccessNode p = new FieldOrPropertyAccessNode(varName);
							p.setKind(FieldKind.PROPERTY);
							p.setType(property.get().getReturningType());

							access.getParent().replace(access, p);

							methodOwnerType = p.getTypeVariable();
						}

					} else {
						VariableReadNode vnode = new VariableReadNode(varName, variableInfo);

						access.getParent().replace(access, vnode);

						methodOwnerType = variableInfo.getTypeVariable();
					}

				}
			} else if (access instanceof VariableReadNode) {
				VariableReadNode var = (VariableReadNode) access;

				Optional<Field> field = currentType.getFieldByName(var.getName());

				if (field.isPresent()) {
					if (!var.getVariableInfo().isInitialized()) {
						throw new CompilationError(access, "Variable " + var.getName() + " was not initialized");
					}

				}

				methodOwnerType = ((TypedNode) access).getTypeVariable();
			} else if (access instanceof TypedNode) {
				methodOwnerType = ((TypedNode) access).getTypeVariable();
			} else if (access instanceof IdentifierNode) {

				VariableInfo variable = this.getSemanticContext().currentScope()
						.searchVariable(((IdentifierNode) access).getId());

				methodOwnerType = variable.getTypeVariable();
			} else {
				throw new CompilationError("Not supported yet");
			}

			TypeDefinition def = methodOwnerType.getTypeDefinition();
			def = this.getSemanticContext().resolveTypeForName(def.getName(), def.getGenericParameters().size()).get();

			MethodParameter[] parameters = asMethodParameters(m.getCall().getArgumentListNode());

			MethodSignature signature = new MethodSignature(name, parameters);

			// if (!(methodOwnerType instanceof FixedTypeVariable)) {
			// throw new UnsupportedOperationException();
			// }

			Optional<Method> method = def.getMethodBySignature(signature);

			if (!method.isPresent()) {

				method = def.getMethodByPromotableSignature(signature);

				if (method.isPresent()) {
					m.setTypeVariable(method.get().getReturningType());
				} else {
					throw new CompilationError(node, "There is not method named '" + name + "' in type '"
							+ def.getName() + "' with arguments " + Arrays.toString(parameters));
					// throw new UnsupportedOperationException();
					// if (!LenseTypeSystem.isAssignableTo(def,
					// LenseTypeSystem.Maybe())) {
					//
					// throw new CompilationError(node, "The method " + name +
					// "(" + Arrays.toString(parameters)
					// + ") is undefined for TypeDefinition " +
					// methodOwnerType);
					// }
					//
					// TypeDefinition innerType =
					// def.getGenericParameters().get(0);
					//
					// method = innerType.getMethodBySignature(signature);
					//
					// if (!method.isPresent()) {
					// throw new CompilationError("The method " + name + "(" +
					// Arrays.toString(parameters)
					// + ") is undefined for TypeDefinition " + innerType);
					// }
					//
					// // transform to call inside the maybe using map
					// TypeDefinition innerCallReturn =
					// method.get().getReturningType();
					// TypeDefinition finalType =
					// LenseTypeSystem.specify(LenseTypeSystem.Maybe(),
					// innerCallReturn);
					//
					// TypeDefinition functionType =
					// LenseTypeSystem.specify(LenseTypeSystem.Function(2),
					// innerType,
					// innerCallReturn);
					//
					// MethodSignature mapSignature = new MethodSignature("map",
					// new MethodParameter(functionType, "it"));
					//
					// Optional<Method> mapMethod =
					// finalType.getMethodBySignature(mapSignature);
					//
					// MethodInvocationNode transform = new
					// MethodInvocationNode(m.getAccess(), "map",
					// new ClassInstanceCreationNode(functionType));
					//
					// m.getParent().replace(m, transform);
					// transform.setTypeVariable(finalType);
				}

			} else {
				Method mthd = method.get();
				m.setTypeVariable(mthd.getReturningType());

				List<CallableMemberMember<Method>> methodParameters = mthd.getParameters();
				if (methodParameters.size() != m.getCall().getArgumentListNode().getChildren().size()) {
					throw new CompilationError(node, "Argument count does not match parameters count");
				}

				for (int i = 0; i < methodParameters.size(); i++) {
					MethodParameter param = (MethodParameter) methodParameters.get(i);
					ArgumentListItemNode arg = (ArgumentListItemNode) m.getCall().getArgumentListNode().getChildren()
							.get(i);
					arg.setExpectedType(param.getType());

				}

			}

		} else if (node instanceof NewInstanceCreationNode) {

			if (node instanceof lense.compiler.ast.LiteralCreation) {
				return;
			}
			NewInstanceCreationNode n = (NewInstanceCreationNode) node;

			TypeDefinition def = n.getTypeNode().getTypeVariable().getTypeDefinition();

			ConstructorParameter[] parameters = n.getArguments() == null ? new ConstructorParameter[0]
					: asConstructorParameters(n.getArguments());

			Optional<Constructor> constructor = def.getConstructorByParameters(parameters);

			if (!constructor.isPresent()) {
				constructor = def.getConstructorByPromotableParameters(parameters);

				if (!constructor.isPresent()) {
					throw new CompilationError(n,
							"Constructor " + def.getName() + "(" + Arrays.toString(parameters) + ") is not defined");
				}
			}

			n.setConstructor(constructor.get());

			if (n.getArguments() != null) {
				List<CallableMemberMember<Constructor>> methodParameters = constructor.get().getParameters();

				if (methodParameters.size() != n.getArguments().getChildren().size()) {
					throw new CompilationError(node, "Argument count does not match parameters count");
				}

				for (int i = 0; i < methodParameters.size(); i++) {
					ConstructorParameter param = (ConstructorParameter) methodParameters.get(i);
					ArgumentListItemNode arg = (ArgumentListItemNode) n.getArguments().getChildren().get(i);
					arg.setExpectedType(param.getType());

				}
			}

		} else if (node instanceof ReturnNode) {
			ReturnNode n = (ReturnNode) node;

			if (this.getSemanticContext().currentScope().getParent() == null) {
				throw new CompilationError(n, "Return clause only can be used inside statments");
			}

			// escape analysis hint
			if (!n.getChildren().isEmpty() && (n.getChildren().get(0) instanceof VariableReadNode)) {
				VariableReadNode vr = (VariableReadNode) n.getChildren().get(0);

				this.getSemanticContext().currentScope().searchVariable(vr.getName()).markEscapes();
			}

			// define variable in the method scope. the current scope is block
			try {
				this.getSemanticContext().currentScope().getParent().defineVariable("@returnOfMethod", n.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {
				// ok. no problem;
			}
		} else if (node instanceof AccessorNode) {

			AccessorNode m = (AccessorNode) node;
			TypeVariable returnType = m.getParent().getType().getTypeVariable();

			if (!m.isAbstract() && !m.isImplicit()) {

				if (returnType != null && returnType.getTypeDefinition().equals(VOID)) {
					VariableInfo variable = this.getSemanticContext().currentScope().searchVariable("@returnOfMethod");

					if (variable != null && !variable.getTypeVariable().equals(VOID)) {
						throw new CompilationError("Method " + m.getParent().getName() + " can not return a value");
					}
				} else {
					VariableInfo variable = this.getSemanticContext().currentScope().searchVariable("@returnOfMethod");

					if (!m.getParent().isNative() && variable == null) {

						LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope()
								.getCurrentType();
						if (!currentType.isNative() && !currentType.isAbstract()
								&& (currentType.getKind() == LenseUnitKind.Class
								|| currentType.getKind() == LenseUnitKind.Object)) {
							throw new CompilationError(node,
									"Method " + m.getParent().getName() + " must return a result of " + returnType);
						}

					}

				}

			}

		} else if (node instanceof MethodDeclarationNode) {

			MethodDeclarationNode m = (MethodDeclarationNode) node;
			TypeVariable returnType = m.getReturnType().getTypeVariable();

			if (!m.isAbstract()) {

				if (!m.getReturnType().needsInference()){
					if (returnType.getTypeDefinition().equals(VOID)) {
						VariableInfo variable = this.getSemanticContext().currentScope().searchVariable("@returnOfMethod");

						if (variable != null && !variable.getTypeVariable().equals(VOID)) {
							throw new CompilationError(node, "Method " + m.getName() + " can not return a value");
						}
					} else {
						VariableInfo variable = this.getSemanticContext().currentScope().searchVariable("@returnOfMethod");

						if (!m.isNative() && variable == null) {

							TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();
							if (currentType.getKind() == LenseUnitKind.Class) {
								throw new CompilationError(node,
										"Method " + m.getName() + " must return a result of " + returnType);
							}
						}

						ReturnNode rn = null;
						for (AstNode r : m.getBlock().getChildren()) {
							if (r instanceof ReturnNode) {
								rn = (ReturnNode) r;
								break;
							}
						}

						if (rn == null) {
							throw new CompilationError(node, variable.getTypeVariable() + " no return found");
						}

						if (!LenseTypeSystem.isAssignableTo(variable.getTypeVariable(), returnType)) {

							if (!LenseTypeSystem.getInstance().isPromotableTo(variable.getTypeVariable(), returnType)) {
								throw new CompilationError(node, variable.getTypeVariable() + " is not assignable to "
										+ returnType + " in the return of method " + m.getName());
							} else {
								// TODO promote

								Optional<Constructor> op = returnType.getTypeDefinition()
										.getConstructorByParameters(new ConstructorParameter(variable.getTypeVariable()));

								ReturnNode nr = new ReturnNode();
								nr.add(NewInstanceCreationNode.of(returnType,op.get(),rn.getChildren().get(0)));

								m.getBlock().replace(rn, nr);
							}
						}

					}

				}

			} else {
				if (this.getSemanticContext().currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
					m.setVisibility(Visibility.Public);
					m.setAbstract(true);
				}

			}


		}  else if (node instanceof ClassTypeNode) {

			ClassTypeNode t = (ClassTypeNode) node;
			if (t.getInterfaces() != null) {

				for (AstNode n : t.getInterfaces().getChildren()) {
					TypeNode tn = (TypeNode) n;
					TypeDefinition typeVariable = ((FixedTypeVariable) tn.getTypeVariable()).getTypeDefinition();
					if (typeVariable.getKind() != LenseUnitKind.Interface) {
						throw new CompilationError(t.getName() + " cannot implement TypeDefinition "
								+ typeVariable.getName() + " because " + typeVariable.getName() + " it is a "
								+ typeVariable.getKind() + " and not an interface");
					}
				}

			}
		} else if (node instanceof ConditionalStatement) {

			if (!((ConditionalStatement) node).getCondition().getTypeVariable().getTypeDefinition()
					.equals(LenseTypeSystem.Boolean())) {
				throw new CompilationError("Condition must be a Boolean value, found "
						+ ((ConditionalStatement) node).getCondition().getTypeVariable().getTypeDefinition().getName());
			}
		} else if (node instanceof ForEachNode) {
			ForEachNode n = (ForEachNode) node;

			if (!LenseTypeSystem.isAssignableTo(n.getContainer().getTypeVariable(),
					new FixedTypeVariable(LenseTypeSystem.Iterable()))) {

				throw new CompilationError(node, "Can only iterate over an instance of " + LenseTypeSystem.Iterable());

			}

			if (!LenseTypeSystem.isAssignableTo(
					n.getContainer().getTypeVariable().getGenericParameters().get(0).getUpperbound(),
					n.getVariableDeclarationNode().getTypeVariable())) {
				throw new CompilationError(n.getVariableDeclarationNode().getTypeVariable().getSymbol()
						+ " is not contained in " + n.getContainer().getTypeVariable());
			}
			if (n.getContainer() instanceof RangeNode) {
				RangeNode range = (RangeNode) n.getContainer();

				ArgumentListItemNode arg = new ArgumentListItemNode(0, range.getEnd());
				arg.setExpectedType(range.getEnd().getTypeVariable());
			
				MethodInvocationNode create = new MethodInvocationNode(range.getStart(), "upTo", arg);
				create.setTypeVariable(new FixedTypeVariable(
						LenseTypeSystem.specify(LenseTypeSystem.Progression(), range.getStart().getTypeVariable())));
				n.replace(range, create);
			}
		} else if (node instanceof ParametersListNode) {

			for (AstNode n : node.getChildren()) {
				FormalParameterNode var = (FormalParameterNode) n;
				// mark this variables as initialized because they are
				// parameters
				this.getSemanticContext().currentScope().searchVariable(var.getName()).setInitialized(true);
			}
		} else if (node instanceof CatchOptionNode) {

			TypeVariable exceptionType = ((CatchOptionNode) node).getExceptions().getTypeVariable();
			if (!LenseTypeSystem.isAssignableTo(exceptionType, new FixedTypeVariable(LenseTypeSystem.Exception()))) {
				throw new CompilationError("No exception of TypeDefinition " + exceptionType.getSymbol()
				+ " can be thrown; an exception TypeDefinition must be a subclass of Exception");
			}

		} else if (node instanceof SwitchOption) {

			final SwitchOption s = (SwitchOption) node;
			if (!s.isDefault()) {
				boolean literal = s.getValue() instanceof LiteralExpressionNode;
				if (!literal) {
					throw new CompilationError("Switch option must be a constant");
				}
			}

		} 
	}

	private void resolveFieldPropertyOrVariableName(AstNode node, FieldOrPropertyAccessNode m, TypeVariable currentType, TypeVariable fieldOwnerType, String name) {
		TypeDefinition def = ((FixedTypeVariable) currentType).getTypeDefinition();
		Optional<Field> field = def.getFieldByName(name);

		if (!field.isPresent()) {

			// try variable
			VariableInfo variable = this.getSemanticContext().currentScope().searchVariable(name);

			if (variable == null) {

				Optional<Property> property = def.getPropertyByName(name);

				if (!property.isPresent()) {
					throw new CompilationError(node, name + " is not defined in " + fieldOwnerType);

				} else {
					m.setTypeVariable(property.get().getReturningType()); // TODO
					// use
					// typevariables
					// all
					// the
					// way
					m.setKind(FieldOrPropertyAccessNode.FieldKind.PROPERTY);
				}
			} else {
				m.setTypeVariable(variable.getTypeVariable());

				m.getParent().replace(m, new VariableReadNode(name, variable));

			}
		} else {
			m.setTypeVariable(field.get().getReturningType());
			m.setKind(FieldOrPropertyAccessNode.FieldKind.FIELD);
		}
	}

	/**
	 * @param methodOwnerType
	 * @return
	 */
	private int countTupleSize(TypeDefinition methodOwnerType) {
		int count = 0;
		TypeVariable type = methodOwnerType.getGenericParameters().get(1).getUpperbound();
		while (!LenseTypeSystem.isAssignableTo(type.getTypeDefinition(), LenseTypeSystem.Nothing())) {
			count++;

			type = type.getGenericParameters().get(1).getUpperbound();
		}
		return count + 1;
	}

	/**
	 * @param indexExpression
	 */
	private Optional<Integer> asConstantNumber(ExpressionNode indexExpression) {
		if (indexExpression instanceof NumericValue) {
			return Optional.of(((NumericValue) indexExpression).getValue().intValue());
		} else {
			return Optional.empty();
		}
	}

	public ConstructorParameter[] asConstructorParameters(ArgumentListNode argumentListNode) {
		MethodParameter[] params = asMethodParameters(argumentListNode);
		ConstructorParameter[] cparams = new ConstructorParameter[params.length];

		for (int i = 0; i < params.length; i++) {
			cparams[i] = new ConstructorParameter(params[i].getType(), params[i].getName());
		}

		return cparams;

	}

	public MethodParameter[] asMethodParameters(ArgumentListNode argumentListNode) {
		return argumentListNode.getChildren().stream().map(a -> ((ArgumentListItemNode) a).getFirstChild()).map(v -> {
			if (v instanceof VariableReadNode) {
				VariableReadNode var = (VariableReadNode) v;
				return new MethodParameter(var.getTypeVariable());
			} else if (v instanceof MethodInvocationNode) {
				MethodInvocationNode var = (MethodInvocationNode) v;
				return new MethodParameter(var.getTypeVariable());
			} else if (v instanceof TypedNode) {
				TypedNode var = (TypedNode) v;
				if (var.getTypeVariable() == null) {

					return null;
					// int index =
					// resolveCurrentTypeGenericTypeParameterIndex(var.getTypeParameter().getName());
					// compiler.typesystem.TypeVariable tv = new
					// MethodDeclaringTypeParameter(index);
					//
					// return new MethodParameter(tv, var());
				} else {
					return new MethodParameter(var.getTypeVariable());
				}

			} else if (v instanceof QualifiedNameNode) {
				QualifiedNameNode qn = (QualifiedNameNode) v;

				Optional<TypeDefinition> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = this.getSemanticContext().resolveTypeForName((qn).getName(), 0);
					} else {
						break;
					}
				}

				if (!maybeType.isPresent()) {
					throw new CompilationError(v, ((QualifiedNameNode) v).getName() + " is not a recognized type");
				}
				return new MethodParameter(maybeType.get());
			} else if (v instanceof IdentifierNode) {
				VariableInfo var = this.getSemanticContext().currentScope().searchVariable(((IdentifierNode) v).getId());

				if (var == null) {
					throw new CompilationError(v, ((IdentifierNode) v).getId() + " is not a field or variable");
				}

				return new MethodParameter(var.getTypeVariable());
			} else {
				throw new RuntimeException();
			}
		}).collect(Collectors.toList()).toArray(new MethodParameter[argumentListNode.getChildren().size()]);
	}



}