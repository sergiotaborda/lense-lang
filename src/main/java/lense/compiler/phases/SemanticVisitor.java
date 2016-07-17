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
import lense.compiler.Visibility;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.AnnotationNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ClassInstanceCreationNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.PreExpression;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.Imutability;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.IntervalNode;
import lense.compiler.ast.LambdaExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ObjectReadNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreBooleanUnaryExpression;
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
import lense.compiler.type.TypeMember;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.LiteralSequenceInstanceCreation ;

public class SemanticVisitor extends AbstractLenseVisitor {

	SemanticContext semanticContext;

	// TODO remove method discovery is done in StructureVisitor
	private Map<String, Set<MethodSignature>> defined = new HashMap<String, Set<MethodSignature>>();
	private Map<String, Set<MethodSignature>> expected = new HashMap<String, Set<MethodSignature>>();

	private LenseTypeDefinition ANY;
	private LenseTypeDefinition VOID;
	// private LenseTypeDefinition NOTHING;
	private LenseTypeDefinition currentType;

	public SemanticVisitor(SemanticContext sc) {
		this.semanticContext = sc;
		ANY = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Any", 0).get();
		VOID = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Void", 0).get();
		// NOTHING = (LenseTypeDefinition)
		// sc.resolveTypeForName("lense.core.lang.Nothing", 0).get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {

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

		if (!currentType.hasConstructor()) {
			// if no constructor exists, add a default one
			currentType.addConstructor(new Constructor("constructor", Collections.emptyList(), false));
		}

		currentType.getConstructors().filter(m -> m.getName() == null)
		.sorted((a, b) -> a.getParameters().size() - b.getParameters().size()).forEach(c -> {

			c.setName("constructor");

		});

		if (!currentType.isAbstract()) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {

		if (node instanceof ConstructorDeclarationNode) {

			ConstructorDeclarationNode constructorDeclarationNode = (ConstructorDeclarationNode) node;

			// defaults
			if (constructorDeclarationNode.getVisibility() == null) {
				constructorDeclarationNode.setVisibility(Visibility.Private);
			}

			applyAnnotations(constructorDeclarationNode);

			semanticContext.beginScope(constructorDeclarationNode.getScopeIdentifer());

			constructorDeclarationNode.setReturnType(new TypeNode(semanticContext.currentScope().getCurrentType()));

		} else if (node instanceof MethodDeclarationNode) {
			semanticContext.beginScope(((MethodDeclarationNode) node).getName());

			MethodDeclarationNode m = (MethodDeclarationNode) node;

			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(Visibility.Private);
			}

			// auto-abstract if interface
			if (semanticContext.currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}
		} else if (node instanceof AccessorNode) {
			semanticContext.beginScope("get");

			AccessorNode m = (AccessorNode) node;
			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(m.getParent().getVisibility());
			}

			if (semanticContext.currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}

			if (m.getParent().isIndexed()) {
				for (AstNode n : ((IndexerPropertyDeclarationNode) m.getParent()).getIndexes().getChildren()) {
					FormalParameterNode var = (FormalParameterNode) n;
					TypeVariable type = var.getTypeNode().getTypeVariable();

					semanticContext.currentScope().defineVariable(var.getName(), type, node).setInitialized(true);
				}
			}

		} else if (node instanceof ModifierNode) {
			semanticContext.beginScope("set");

			ModifierNode m = (ModifierNode) node;
			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(m.getParent().getVisibility());
			}
			if (semanticContext.currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
				m.setAbstract(true);
			}
			if (m.getParent().isIndexed()) {
				for (AstNode n : ((IndexerPropertyDeclarationNode) m.getParent()).getIndexes().getChildren()) {
					FormalParameterNode var = (FormalParameterNode) n;
					TypeVariable type = var.getTypeNode().getTypeVariable();

					semanticContext.currentScope().defineVariable(var.getName(), type, node).setInitialized(true);
				}
			}

			semanticContext.currentScope()
			.defineVariable(m.getValueVariableName(), m.getParent().getType().getTypeVariable(), node)
			.setInitialized(true);

		} else if (node instanceof ClassTypeNode) {
			ClassTypeNode t = (ClassTypeNode) node;

			semanticContext.beginScope(t.getName());

			int genericParametersCount = t.getGenerics() == null ? 0 : t.getGenerics().getChildren().size();

			Optional<TypeDefinition> maybeMyType = semanticContext.resolveTypeForName(t.getName(),
					genericParametersCount);

			LenseTypeDefinition myType;
			if (maybeMyType.isPresent()) {
				myType = (LenseTypeDefinition) maybeMyType.get();
			} else {
				myType = new LenseTypeDefinition(t.getName(), t.getKind(), ANY);
				myType = (LenseTypeDefinition) semanticContext.registerType(myType, genericParametersCount);
			}

			myType.setKind(t.getKind());

			// Default
			myType.setVisibility(Visibility.Private);

			for (AstNode n : t.getAnnotations().getChildren()){
				AnnotationNode a = (AnnotationNode)n;
				if (a.getName().equals("abstract")){
					myType.setAbstract(true);
				}else if (a.getName().equals("native")){
					myType.setNative(true);
				}else if (a.getName().equals("public")){
					myType.setVisibility(Visibility.Public);
				}else if (a.getName().equals("protected")){
					myType.setVisibility(Visibility.Protected);
				}else if (a.getName().equals("private")){
					myType.setVisibility(Visibility.Private);
				}
			}

			List<VariableInfo> myGenericTypes = new ArrayList<>();
			if (t.getGenerics() != null) {

				for (AstNode n : t.getGenerics().getChildren()) {
					GenericTypeParameterNode g = (GenericTypeParameterNode) n;

					TypeNode tn = g.getTypeNode();
					FixedTypeVariable typeVar = new FixedTypeVariable(ANY);

					myGenericTypes.add(semanticContext.currentScope().defineTypeVariable(tn.getName(), typeVar, t));

					// already done in naming phase
					//myType.addGenericParameter(tn.getName(), typeVar);
				}

			}

			TypeNode superTypeNode = t.getSuperType();
			TypeDefinition superType = ANY;
			if (superTypeNode != null) {
				superType = semanticContext.typeForName(superTypeNode.getName(),
						superTypeNode.getTypeParametersCount());

				if (superType.isGeneric()) {

					for (AstNode n : superTypeNode.getChildren()) {
						if (n instanceof GenericTypeParameterNode) {

						} else {
							TypeNode tn = (TypeNode) n;
							TypeDefinition rawInterfaceType = semanticContext.typeForName(tn.getName(),tn.getTypeParametersCount());
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
											parameters[index] = new DeclaringTypeBoundedTypeVariable(myType, i, tt.getName(),
													g.getVariance()).toIntervalTypeVariable();
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

			TreeTransverser.transverse(t, new StructureVisitor(myType, semanticContext));

			t.setTypeDefinition(myType);

			currentType = myType;

			semanticContext.currentScope().defineVariable("this", new FixedTypeVariable(myType), node)
			.setInitialized(true);

			semanticContext.currentScope().defineVariable("super", new FixedTypeVariable(superType), node)
			.setInitialized(true);

			if (t.getInterfaces() != null) {
				for (AstNode n : t.getInterfaces().getChildren()) {
					generifyInterfaceType( myType, myGenericTypes,(TypeNode) n);

				}
			}

		} else if (node instanceof BlockNode) {
			semanticContext.beginScope("block");
		} else if (node instanceof VariableReadNode) {
			VariableReadNode v = (VariableReadNode) node;
			VariableInfo variableInfo = semanticContext.currentScope().searchVariable(v.getName());
			if (variableInfo == null) {
				throw new CompilationError(node, "Variable " + v.getName() + " was not defined");
			}
			if (!variableInfo.isInitialized()) {

				throw new CompilationError(node, "Variable " + v.getName() + " was not initialized.");
			}
			v.setVariableInfo(variableInfo);

		} else if (node instanceof ForEachNode) {
			semanticContext.beginScope("for");

			ForEachNode n = (ForEachNode) node;

			semanticContext.currentScope().defineVariable(n.getVariableDeclarationNode().getName(),
					n.getVariableDeclarationNode().getTypeVariable(), n).setInitialized(true);

		} else if (node instanceof VariableWriteNode) {
			VariableWriteNode v = (VariableWriteNode) node;
			VariableInfo variableInfo = semanticContext.currentScope().searchVariable(v.getName());

			if (variableInfo == null) {
				throw new CompilationError("Variable " + v.getName() + " was not defined");
			} else if (variableInfo.getDeclaringNode() instanceof ClassTypeNode) {
				// a field is being set
				// TODO
			}

			variableInfo.markWrite();
			v.setVariableInfo(variableInfo);

		} else if (node instanceof LambdaExpressionNode) {
			semanticContext.beginScope("lambda$" + ((LambdaExpressionNode) node).getLambdaId());

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

				semanticContext.currentScope().defineVariable(name, td, node).setInitialized(true);
				index++;
			}
		}

		return VisitorNext.Children;
	}

	private void generifyInterfaceType(LenseTypeDefinition parentType, List<VariableInfo> genericTypes,TypeNode tn) {

		TypeDefinition rawInterfaceType = semanticContext.typeForName(tn.getName(), tn.getTypeParametersCount());
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
							parameters[index] = new DeclaringTypeBoundedTypeVariable(parentType, i, tt.getName(), g.getVariance()).toIntervalTypeVariable();
						}
					}
				} else {
					if (tt.getTypeParametersCount() > 0){
						// Recursive call
						generifyInterfaceType(null, genericTypes, tt);
						parameters[index] = new FixedTypeVariable(tt.getTypeVariable().getTypeDefinition()).toIntervalTypeVariable();
					} else {
						parameters[index] = tt.getTypeVariable().toIntervalTypeVariable();
					}
				}

				index++;
			}

			interfaceType = LenseTypeSystem.specify(rawInterfaceType, parameters);
			tn.setTypeVariable(new FixedTypeVariable(interfaceType));

			if (parentType != null){
				parentType.addInterface(interfaceType);
				semanticContext.registerType(parentType, genericTypes.size());
			}

		} else {
			tn.setTypeVariable(new FixedTypeVariable(interfaceType));
			parentType.addInterface(interfaceType);
		}
	}

	private void applyAnnotations(ConstructorDeclarationNode c) {
		if (c.getAnnotations() != null && c.getAnnotations().getChildren() != null) {
			for (AstNode a : c.getAnnotations().getChildren()) {
				AnnotationNode n = (AnnotationNode) a;
				if ("public".equals(n.getName())) {
					c.setVisibility(Visibility.Public);
				} else if ("protected".equals(n.getName())) {
					c.setVisibility(Visibility.Protected);
				} else if ("private".equals(n.getName())) {
					c.setVisibility(Visibility.Private);
				} else if ("implicit".equals(n.getName())) {
					c.setImplicit(true);
				} else if ("native".equals(n.getName())) {
					c.setNative(true);
				} else if ("abstract".equals(n.getName())) {
					c.setAbstract(true);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof TypeNode) {
			TypeNode t = (TypeNode) node;
			resolveTypeDefinition(t);

		} else if (node instanceof LiteralSequenceInstanceCreation){
			LiteralSequenceInstanceCreation literal = (LiteralSequenceInstanceCreation)node;
			TypeDefinition maxType = ((TypedNode)literal.getArguments().getChildren().get(0)).getTypeVariable().getTypeDefinition();
			maxType = semanticContext.resolveTypeForName(maxType.getName(), maxType.getGenericParameters().size()).get();

			for( int i =1; i <literal.getArguments().getChildren().size(); i++){
				AstNode n = literal.getArguments().getChildren().get(i);
				TypedNode t = (TypedNode)n;
				TypeDefinition type =  t.getTypeVariable().getTypeDefinition();
				type = semanticContext.resolveTypeForName(type.getName(), type.getGenericParameters().size()).get();
				if (!type.equals(maxType)){
					if (LenseTypeSystem.getInstance().isPromotableTo(maxType, type)){
						maxType = type;
					} else if (!LenseTypeSystem.getInstance().isPromotableTo(type, maxType)){
						// TODO incompatible types in the same array
						throw new CompilationError(node, "Heterogeneous Sequence");
					}
				}
			}

			ListIterator<AstNode> lstIterator = literal.getArguments().getChildren().listIterator();

			FixedTypeVariable maxTypeDef = new FixedTypeVariable(maxType);
			while(lstIterator.hasNext()){
				AstNode n = lstIterator.next();
				TypeDefinition type = ((TypedNode)n).getTypeVariable().getTypeDefinition();
				if (!type.equals(maxType)){
					if (LenseTypeSystem.getInstance().isPromotableTo(type, maxType)){

						Optional<Constructor> op = maxType.getConstructorByParameters(new ConstructorParameter(type));


						final ClassInstanceCreationNode c = new ClassInstanceCreationNode( maxTypeDef,  n);
						c.setConstructor(op.get());

						lstIterator.set(c);


					} 
				}
			}

			literal.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Sequence(), maxType)));

			//		} else if (node instanceof LiteralAssociationInstanceCreation){
			//				
		} else if (node instanceof LiteralTupleInstanceCreation) {
			LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);
			TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0);
			TypedNode nextTuple;
			if (tuple.getChildren().get(1).getChildren().size() == 2) {
				nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1);
			} else {
				nextTuple = new TypeNode(LenseTypeSystem.Nothing());
			}

			TypeNode typeNode = new TypeNode(new QualifiedNameNode(tuple.getTypeNode().getName()));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable())));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));

			typeNode.setTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Tuple(), value.getTypeVariable(),
					nextTuple.getTypeVariable()));

			tuple.replace(tuple.getTypeNode(), typeNode);
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
		} else if (node instanceof IntervalNode) {
			IntervalNode r = (IntervalNode) node;

			Optional<TypeVariable> oleft = Optional.ofNullable(r.getStart()).map(s -> s.getTypeVariable());
			Optional<TypeVariable> oright = Optional.ofNullable(r.getEnd()).map(s -> s.getTypeVariable());

			TypeVariable finalType = null;
			if (oleft.isPresent() && oright.isPresent()) {
				TypeVariable left = oleft.get();
				TypeVariable right = oright.get();

				if (left.equals(right)) {
					finalType = left;
				} else if (LenseTypeSystem.getInstance().isPromotableTo(left, right)) {
					finalType = right;
				} else if (LenseTypeSystem.getInstance().isPromotableTo(right, left)) {
					finalType = left;
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

			r.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Progression(), finalType)));
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

			semanticContext.endScope();
		} else if (node instanceof ArithmeticNode) {
			ArithmeticNode n = (ArithmeticNode) node;

			TypeVariable left = n.getLeft().getTypeVariable();
			TypeVariable right = n.getRight().getTypeVariable();

			if (left.equals(right)) {
				n.setTypeVariable(left);

				if (left.getTypeDefinition().equals(LenseTypeSystem.String())){
					StringConcatenationNode c;
					if (n.getLeft() instanceof StringConcatenationNode){
						c = (StringConcatenationNode)n.getLeft();
						c.add(n.getRight());
					} else {
						c= new StringConcatenationNode();
						c.add(n.getLeft());
						c.add(n.getRight());

					}
					c.setTypeVariable(left);
					n.getParent().replace(n, c);
				}
			} else if (left instanceof FixedTypeVariable) {
				// find instance operator method
				TypeDefinition type = ((FixedTypeVariable) left).getTypeDefinition();

				if (type.equals(LenseTypeSystem.String())){

					MethodInvocationNode convert = new MethodInvocationNode(n.getRight(),"asString");

					StringConcatenationNode concat = new StringConcatenationNode();
					concat.add(n.getLeft());
					concat.add(convert);
					concat.setTypeVariable(left);

					n.getParent().replace(node, concat);
				} else {
					MethodSignature signature = new MethodSignature(n.getOperation().equivalentMethod(),new MethodParameter(right));

					Optional<Method> method = type.getMethodBySignature(signature);

					if (!method.isPresent()) {

						method = type.getMethodByPromotableSignature(signature);

						if (!method.isPresent()) {
							// search static operator
							throw new CompilationError(node,"Method " + n.getOperation().equivalentMethod() + "(" + right
									+ ") is not defined in " + left);
						} else {
							// Promote
							Optional<Constructor> op = left.getTypeDefinition().getConstructorByParameters(new ConstructorParameter(right));

							final ClassInstanceCreationNode c = new ClassInstanceCreationNode( left,  n.getRight());
							c.setConstructor(op.get());

							n.replace(n.getRight(), c);
						}
					}

					MethodInvocationNode invokeOp = new MethodInvocationNode(n.getLeft(),
							n.getOperation().equivalentMethod(), n.getRight());
					invokeOp.setTypeVariable(method.get().getReturningType());

					n.getParent().replace(node, invokeOp);
				}
			} else {
				throw new RuntimeException("Cannot determine Arithmetic value type");
			}

		} else if (node instanceof PosExpression) {
			PosExpression p = (PosExpression) node;

			if (p.getOperation().equals(ArithmeticOperation.Subtraction)) { /* -a */

				TypeVariable variable = ((TypedNode) p.getChildren().get(0)).getTypeVariable();

				if (variable instanceof FixedTypeVariable) {
					final TypeDefinition type = ((FixedTypeVariable) variable).getTypeDefinition();
					Optional<Method> list = type.getMethodsByName("negative").stream()
							.filter(md -> md.getParameters().size() == 0).findAny();

					if (!list.isPresent()) {
						throw new CompilationError("The method negative() is undefined for TypeDefinition " + type);
					}
				}

			} else if (p.getOperation().equals(ArithmeticOperation.Addition)) { /* +a */
				// nothing , only remove the pos expression from the node tree
				// /node.getParent().replace(node, node.getChildren().get(0));
			} else {
				throw new CompilationError(node, "Unrecognized operator");
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
			} else if (p.getOperation().equals(ArithmeticOperation.Addition)){ /* +a */
				// +a is a no-op. replace the node by its content
				node.getParent().replace(node, node.getChildren().get(0));
			} else {
				throw new CompilationError(node,
						"There is no unary " + p.getOperation().equivalentMethod() + " operation.");
			}
		} else if (node instanceof LiteralExpressionNode) {
			LiteralExpressionNode n = (LiteralExpressionNode)node;

			n.setTypeVariable(new FixedTypeVariable(semanticContext.resolveTypeForName(n.getTypeVariable().getTypeDefinition().getName(), 0).get()));

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
					throw new CompilationError(right + " is not assignable to " + left);
				} else {
					// TODO change to promote node, promotion is implicit
					// constructor based
					Optional<Constructor> op = left.getTypeDefinition().getConstructorByParameters(new ConstructorParameter(right));

					final ClassInstanceCreationNode m = new ClassInstanceCreationNode( left, n.getRight());
					m.setConstructor(op.get());
					n.replace((AstNode) n.getRight(), m);
				}
			}

			if (n.getLeft() instanceof VariableWriteNode) {
				VariableInfo info = semanticContext.currentScope()
						.searchVariable(((VariableWriteNode) n.getLeft()).getName());

				if (info.isImutable() && info.isInitialized()) {
					throw new CompilationError(node,
							"Cannot modify the value of an imutable variable or field (" + info.getName() + ")");
				}
				info.setInitialized(true);
			} else if (n.getLeft() instanceof FieldOrPropertyAccessNode) {
				VariableInfo info = semanticContext.currentScope()
						.searchVariable(((FieldOrPropertyAccessNode) n.getLeft()).getName());

				if (info.isImutable() && info.isInitialized()) {

					AstNode parent = ((LenseAstNode) n.getLeft()).getParent().getParent().getParent();
					if (!(parent instanceof ConstructorDeclarationNode)) {
						throw new CompilationError(node,
								"Cannot modify the value of an imutable variable or field (" + info.getName() + ")");
					}

				}
				info.setInitialized(true);
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
				semanticContext.currentScope().defineVariable(formal.getName(), formal.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {

			}
		} else if (node instanceof ScopedVariableDefinitionNode) {
			ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode) node;
			TypeVariable type = variableDeclaration.getTypeVariable();

			VariableInfo info = semanticContext.currentScope().searchVariable(variableDeclaration.getName());

			if (info == null){
				try {
					info = semanticContext.currentScope().defineVariable(variableDeclaration.getName(), type, node);
				} catch (TypeAlreadyDefinedException e) {
					if (!(node.getParent() instanceof ForEachNode)){
						throw new CompilationError(node, e.getMessage());
					}
				}
			}

			info.setImutable(variableDeclaration.getImutabilityValue() == Imutability.Imutable);

			variableDeclaration.setInfo(info);

			TypedNode init = variableDeclaration.getInitializer();

			if (init != null) {

				info.setInitialized(true);
				TypeVariable right = init.getTypeVariable();

				if (!LenseTypeSystem.isAssignableTo(right, type)) {
					if (LenseTypeSystem.getInstance().isPromotableTo(right, type)) {
						// TODO use promote node
						Optional<Constructor> op = type.getTypeDefinition().getConstructorByParameters(new ConstructorParameter(right));

						final ClassInstanceCreationNode m = new ClassInstanceCreationNode( type, variableDeclaration.getInitializer());
						m.setConstructor(op.get());

						variableDeclaration.setInitializer(m);

					} else {
						throw new CompilationError(node,
								right + " is not assignable to variable '" + info.getName() + "' of type " + type);
					}
				}
			}

			if (node instanceof FieldDeclarationNode) {
				FieldDeclarationNode f = (FieldDeclarationNode) node;

				LenseTypeDefinition currentType = (LenseTypeDefinition) semanticContext.currentScope().getCurrentType();

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

			// auto-abstract if interface
			if (semanticContext.currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
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

			LenseTypeDefinition currentType = (LenseTypeDefinition) semanticContext.currentScope().getCurrentType();

			String typeName = p.getType().getName();
			VariableInfo genericParameter = semanticContext.currentScope().searchVariable(typeName);

			if (genericParameter != null && genericParameter.isTypeVariable()) {
				List<IntervalTypeVariable> parameters = currentType.getGenericParameters();
				Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);

				if (!opIndex.isPresent()) {
					throw new CompilationError(node,
							typeName + " is not a valid generic parameter for type " + currentType.getName());
				}

				int index = opIndex.get();

				DeclaringTypeBoundedTypeVariable pp = new DeclaringTypeBoundedTypeVariable(currentType, index,typeName,
						parameters.get(index).getVariance());

				if (p.isIndexed()) {

					lense.compiler.type.variable.TypeVariable[] params = new lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode) p)
					                                                                                                   .getIndexes().getChildren().size()];
					int i = 0;
					for (AstNode n : ((IndexerPropertyDeclarationNode) p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						TypeVariable type = var.getTypeNode().getTypeVariable();
						params[i++] = type;
						// semanticContext.currentScope().defineTypeVariable(var.getName(),
						// type, p).setInitialized(true);
					}

					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}

			} else {
				Optional<TypeDefinition> type = semanticContext.resolveTypeForName(p.getType().getName(),
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
						// semanticContext.currentScope().defineTypeVariable(var.getName(),
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

			TypeDefinition currentType = semanticContext.currentScope().getCurrentType();

			TypeDefinition methodOwnerType = currentType;
			if (a != null) {
				if (a.getTypeVariable() instanceof FixedTypeVariable) {
					methodOwnerType = a.getTypeVariable().getTypeDefinition();
				} else {
					throw new UnsupportedOperationException();
				}

			}

			if (methodOwnerType.getName().equals("lense.core.collections.Tuple")) {
				Optional<Integer> index = asConstantNumber(m.getIndexExpression());
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

						MethodInvocationNode invoke = new MethodInvocationNode(previous, "head");

						node.getParent().replace(node, invoke);

						invoke.setTypeVariable(previous.getTypeVariable().getGenericParameters().get(0).getUpperbound());
						return;
					}

				}
			}

			TypeVariable type = m.getIndexExpression().getTypeVariable();

			Optional<IndexerProperty> indexer = methodOwnerType.getIndexerPropertyByTypeArray(new TypeVariable[] { type });

			if (!indexer.isPresent()) {
				throw new CompilationError(node, "No indexer [" + m.getIndexExpression().getTypeVariable()
						+ "] is defined for type " + methodOwnerType);
			}

			m.setTypeVariable(indexer.get().getReturningType());

		} else if (node instanceof PosExpression) {
			PosExpression n = (PosExpression) node;
			n.setTypeVariable(((TypedNode) n.getChildren().get(0)).getTypeVariable());
		} else if (node instanceof FieldOrPropertyAccessNode) {
			FieldOrPropertyAccessNode m = (FieldOrPropertyAccessNode) node;

			VariableInfo info = semanticContext.currentScope().searchVariable("this");
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

				Optional<TypeDefinition> maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = semanticContext.resolveTypeForName((qn).getName(), 0);
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
					VariableInfo variable = semanticContext.currentScope()
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
			} else if (access instanceof IdentifierNode) {

				VariableInfo variable = semanticContext.currentScope()
						.searchVariable(((IdentifierNode) access).getId());

				fieldOwnerType = variable.getTypeVariable();
			} else {
				throw new CompilationError(access.getClass() + " Not supported yet");
			}

			if (fieldOwnerType.equals(currentType)) {

				TypeDefinition def = ((FixedTypeVariable) currentType).getTypeDefinition();
				Optional<Field> field = def.getFieldByName(name);

				if (!field.isPresent()) {

					Optional<Property> property = def.getPropertyByName(name);

					if (!property.isPresent()) {
						// try variable
						VariableInfo variable = semanticContext.currentScope().searchVariable(name);

						if (variable == null) {
							throw new CompilationError(node, name + " is not defined in " + fieldOwnerType);
						}

						m.setTypeVariable(variable.getTypeVariable());

						m.getParent().replace(m, new VariableReadNode(name, variable));
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
					m.setTypeVariable(field.get().getReturningType());
					m.setKind(FieldOrPropertyAccessNode.FieldKind.FIELD);
				}

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

						MethodInvocationNode transform = new MethodInvocationNode(m.getPrimary(), "map",
								new ClassInstanceCreationNode(new FixedTypeVariable(mappingFunction)) // TODO
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

			ListIterator<AstNode> it = m.getChildren().listIterator();

			while (it.hasNext()) {
				AstNode a = it.next();
				if (a instanceof IdentifierNode) {
					IdentifierNode id = (IdentifierNode) a;
					VariableInfo info = semanticContext.currentScope().searchVariable(id.getId());

					if (info == null) {
						// try field

						TypeDefinition currentType = semanticContext.currentScope().getCurrentType();

						Optional<Field> field = currentType.getFieldByName(id.getId());

						if (!field.isPresent()) {
							throw new CompilationError(id, id.getId() + " is not a variable or a field");
						}
						FieldOrPropertyAccessNode r = new FieldOrPropertyAccessNode(id.getId());
						r.setType(field.get().getReturningType());
						it.set(r);

					} else {
						VariableReadNode r = new VariableReadNode(id.getId(), info);
						it.set(r);
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

			TypeVariable methodOwnerType = semanticContext.currentScope().searchVariable("this").getTypeVariable();
			TypeDefinition currentType = semanticContext.currentScope().getCurrentType();

			String name = m.getCall().getName();

			AstNode access = m.getAccess();

			if (access == null) {
				// access to self
				MethodParameter[] parameters = asMethodParameters(m.getCall().getArgumentListNode().getChildren());
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

				Optional<TypeDefinition> maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = semanticContext.resolveTypeForName((qn).getName(), 0);
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

					if (def.getKind() == LenseUnitKind.Object){
						ObjectReadNode vnode = new ObjectReadNode(def,(qn).getName());

						access.getParent().replace(access, vnode);

						methodOwnerType = vnode.getTypeVariable();
					}

				} else {
					// try variable
					String varName = ((QualifiedNameNode) access).getName();
					VariableInfo variableInfo = semanticContext.currentScope().searchVariable(varName);

					if (variableInfo == null) {

						Optional<TypeDefinition> obj = semanticContext.resolveTypeForName(varName, 0);

						if (obj.isPresent() && obj.get().getKind() == LenseUnitKind.Object){
							ObjectReadNode vnode = new ObjectReadNode(obj.get(), varName);

							access.getParent().replace(access, vnode);

							methodOwnerType = vnode.getTypeVariable();
						}

						// try a super property
						Optional<Property> property = semanticContext.currentScope().getCurrentType().getSuperDefinition().getPropertyByName(varName);

						if (!property.isPresent()){
							throw new CompilationError(
									((QualifiedNameNode) access).getName() + " is not a valid field, property or object");
						} else {
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

				VariableInfo variable = semanticContext.currentScope()
						.searchVariable(((IdentifierNode) access).getId());

				methodOwnerType = variable.getTypeVariable();
			} else {
				throw new CompilationError("Not supported yet");
			}

			MethodParameter[] parameters = asMethodParameters(m.getCall().getArgumentListNode().getChildren());

			MethodSignature signature = new MethodSignature(name, parameters);

			if (!(methodOwnerType instanceof FixedTypeVariable)) {
				throw new UnsupportedOperationException();
			}

			TypeDefinition def = methodOwnerType.getTypeDefinition();
			Optional<Method> method = def.getMethodBySignature(signature);

			if (!method.isPresent()) {

				method = def.getMethodByPromotableSignature(signature);

				if (method.isPresent()) {
					m.setTypeVariable(method.get().getReturningType());
				} else {
					throw new CompilationError(node, "There is not method named '" + name + "' in type '" + def.getName() + "' with arguments " + Arrays.toString(parameters));
					//throw new UnsupportedOperationException();
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
				Method mdth = method.get();
				m.setTypeVariable(mdth.getReturningType());
			}

		} else if (node instanceof ClassInstanceCreationNode) {

			if (node instanceof lense.compiler.ast.LiteralCreation){
				return;
			}
			ClassInstanceCreationNode n = (ClassInstanceCreationNode) node;

			TypeDefinition def = n.getTypeNode().getTypeVariable().getTypeDefinition();

			ConstructorParameter[] parameters = n.getArguments() == null ? new ConstructorParameter[0]
					: asConstructorParameters(n.getArguments().getChildren());

			Optional<Constructor> constructor = def.getConstructorByParameters(parameters);

			if (!constructor.isPresent()) {
				constructor = def.getConstructorByPromotableParameters(parameters);

				if (!constructor.isPresent()) {
					throw new CompilationError(n,
							"Constructor " + def.getName() + "(" + Arrays.toString(parameters) + ") is not defined");
				}
			}

			n.setConstructor(constructor.get());

		} else if (node instanceof ReturnNode) {
			ReturnNode n = (ReturnNode) node;

			if (semanticContext.currentScope().getParent() == null) {
				throw new RuntimeException("Cannot exist return in master scope");
			}

			if (!n.getChildren().isEmpty() && (n.getChildren().get(0) instanceof VariableReadNode)) {
				VariableReadNode vr = (VariableReadNode) n.getChildren().get(0);

				semanticContext.currentScope().searchVariable(vr.getName()).markEscapes();
			}

			// define variable in the method scope. the current scope is block
			try {
				semanticContext.currentScope().getParent().defineVariable("@returnOfMethod", n.getTypeVariable(), node);
			} catch (TypeAlreadyDefinedException e) {
				// ok. no problem;
			}
		} else if (node instanceof AccessorNode) {

			AccessorNode m = (AccessorNode) node;
			TypeVariable returnType = m.getParent().getType().getTypeVariable();

			if (!m.isAbstract()) {

				if (returnType != null && returnType.getTypeDefinition().equals(VOID)) {
					VariableInfo variable = semanticContext.currentScope().searchVariable("@returnOfMethod");

					if (variable != null && !variable.getTypeVariable().equals(VOID)) {
						throw new CompilationError("Method " + m.getParent().getName() + " can not return a value");
					}
				} else {
					VariableInfo variable = semanticContext.currentScope().searchVariable("@returnOfMethod");

					if (!m.getParent().isNative() && variable == null) {

						LenseTypeDefinition currentType = (LenseTypeDefinition) semanticContext.currentScope().getCurrentType();
						if (!currentType.isNative() && !currentType.isAbstract() && ( currentType.getKind() == LenseUnitKind.Class || currentType.getKind() == LenseUnitKind.Object)) {
							throw new CompilationError(node,
									"Method " + m.getParent().getName() + " must return a result of " + returnType);
						}

					}

				}

			}
			semanticContext.endScope();

		} else if (node instanceof ModifierNode) {
			semanticContext.endScope();

		} else if (node instanceof MethodDeclarationNode) {

			MethodDeclarationNode m = (MethodDeclarationNode) node;
			TypeVariable returnType = m.getReturnType().getTypeVariable();

			if (!m.isAbstract()) {

				if (returnType.getTypeDefinition().equals(VOID)) {
					VariableInfo variable = semanticContext.currentScope().searchVariable("@returnOfMethod");

					if (variable != null && !variable.getTypeVariable().equals(VOID)) {
						throw new CompilationError(node, "Method " + m.getName() + " can not return a value");
					}
				} else {
					VariableInfo variable = semanticContext.currentScope().searchVariable("@returnOfMethod");

					if (!m.isNative() && variable == null) {

						TypeDefinition currentType = semanticContext.currentScope().getCurrentType();
						if (currentType.getKind() == LenseUnitKind.Class) {
							throw new CompilationError(node,
									"Method " + m.getName() + " must return a result of " + returnType);
						}
					}

					if (!LenseTypeSystem.isAssignableTo(variable.getTypeVariable(), returnType)) {

						if (!LenseTypeSystem.getInstance().isPromotableTo(variable.getTypeVariable(), returnType)) {
							throw new CompilationError(node, variable.getTypeVariable() + " is not assignable to "
									+ returnType + " in the return of method " + m.getName());
						} else {
							// TODO promote
							ReturnNode rn= null;
							for( AstNode r : m.getBlock().getChildren()){
								if (r instanceof ReturnNode){
									rn = (ReturnNode) r;
									break;
								}
							}

							if (rn == null){
								throw new CompilationError(node, variable.getTypeVariable() + " no return found");
							}
							Optional<Constructor> op = returnType.getTypeDefinition().getConstructorByParameters(new ConstructorParameter(variable.getTypeVariable()));

							final ClassInstanceCreationNode c = new ClassInstanceCreationNode( returnType,  rn.getChildren().get(0));
							c.setConstructor(op.get());

							ReturnNode nr = new ReturnNode();
							nr.add(c);

							m.getBlock().replace(rn, nr);
						}

					}
				}

			} else {
				if (semanticContext.currentScope().getCurrentType().getKind() == LenseUnitKind.Interface) {
					m.setVisibility(Visibility.Public);
					m.setAbstract(true);
				}

			}

			semanticContext.endScope();
		} else if (node instanceof ConstructorDeclarationNode) {
			semanticContext.endScope();
		} else if (node instanceof ClassTypeNode) {
			semanticContext.endScope();
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

			if (!((ConditionalStatement) node).getCondition().getTypeVariable().getTypeDefinition().equals(LenseTypeSystem.Boolean())) {
				throw new CompilationError("Condition must be a Boolean value, found " + ((ConditionalStatement) node).getCondition().getTypeVariable().getTypeDefinition().getName());
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

				MethodInvocationNode create = new MethodInvocationNode(range.getStart(), "upTo", range.getEnd());
				create.setTypeVariable(new FixedTypeVariable(
						LenseTypeSystem.specify(LenseTypeSystem.Progression(), range.getStart().getTypeVariable())));
				n.replace(range, create);
			}
			semanticContext.endScope();
		} else if (node instanceof ParametersListNode) {

			for (AstNode n : node.getChildren()) {
				FormalParameterNode var = (FormalParameterNode) n;
				// mark this variables as initialized because they are
				// parameters
				semanticContext.currentScope().searchVariable(var.getName()).setInitialized(true);
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

		} else if (node instanceof BlockNode) {
			semanticContext.endScope();
		}
	}

	/**
	 * @param methodOwnerType
	 * @return
	 */
	private int countTupleSize(TypeDefinition methodOwnerType) {
		int count = 0;
		TypeVariable type = methodOwnerType.getGenericParameters().get(1).getUpperbound();
		while (!LenseTypeSystem.isAssignableTo(type.getTypeDefinition(), LenseTypeSystem.Nothing())){
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

	public ConstructorParameter[] asConstructorParameters(List<AstNode> nodes) {
		MethodParameter[] params = asMethodParameters(nodes);
		ConstructorParameter[] cparams = new ConstructorParameter[params.length];

		for (int i = 0; i < params.length; i++) {
			cparams[i] = new ConstructorParameter(params[i].getType(), params[i].getName());
		}

		return cparams;

	}

	public MethodParameter[] asMethodParameters(List<AstNode> nodes) {
		return nodes.stream().map(v -> {
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

				Optional<TypeDefinition> maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);

				while (!maybeType.isPresent()) {
					qn = qn.getPrevious();
					if (qn != null) {
						maybeType = semanticContext.resolveTypeForName((qn).getName(), 0);
					} else {
						break;
					}
				}

				if (!maybeType.isPresent()) {
					throw new CompilationError(v, ((QualifiedNameNode) v).getName() + " is not a recognized type");
				}
				return new MethodParameter(maybeType.get());
			} else if (v instanceof IdentifierNode) {
				VariableInfo var = semanticContext.currentScope().searchVariable(((IdentifierNode) v).getId());

				if (var == null) {
					throw new CompilationError(v, ((IdentifierNode) v).getId() + " is not a field or variable");
				}

				return new MethodParameter(var.getTypeVariable());
			} else {
				throw new RuntimeException();
			}
		}).collect(Collectors.toList()).toArray(new MethodParameter[nodes.size()]);
	}

	/**
	 * @param p
	 * @return
	 */
	// private TypeNode ensureTypeNode(AstNode p) {
	// if (p instanceof TypeNode) {
	// return (TypeNode) p;
	// } else {
	// return ((lense.compiler.ast.GenericTypeParameterNode) p).getTypeNode();
	// }
	// }

	/**
	 * @param name
	 * @param argumentListNode
	 */
	// private void markToFind(TypeDefinition declaringType, String name,
	// ArgumentListNode arguments) {
	// Set<MethodSignature> signatures = expected.get(name);
	// if (signatures == null) {
	// signatures = new HashSet<>();
	// expected.put(name, signatures);
	// }
	//
	// MethodParameter[] params = arguments == null ? new MethodParameter[0]
	// : new MethodParameter[arguments.getChildren().size()];
	// for (int i = 0; i < params.length; i++) {
	// TypedNode var = (TypedNode) arguments.getChildren().get(i);
	// params[i] = new MethodParameter(var.getTypeDefinition());
	// }
	// final MethodSignature methodSignature = new MethodSignature(name,
	// params);
	// signatures.add(methodSignature);
	// }

	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}

}