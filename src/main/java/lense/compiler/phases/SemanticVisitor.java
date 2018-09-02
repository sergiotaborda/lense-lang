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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import compiler.parser.IdentifierNode;
import compiler.parser.NameIdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.TypeAlreadyDefinedException;
import lense.compiler.asm.LoadedLenseTypeDefinition;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.BreakNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.LambdaExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.ast.LiteralIntervalNode;
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
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.UnitaryOperation;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.crosscompile.PrimitiveBooleanValue;
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
import lense.compiler.type.TypeMember;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public final class SemanticVisitor extends AbstractScopedVisitor {

	private Map<String, List<Method>> expectedMethods = new HashMap<String, List<Method>>();

	private LenseTypeDefinition ANY;
	private LenseTypeDefinition VOID;
	// private LenseTypeDefinition NOTHING;
	protected LenseTypeDefinition currentType;

	private Map<TypeVariable, List<TypeDefinition>> enhancements = new HashMap<>();

	private final LenseTypeSystem lenseTypeSystem;
	
	public SemanticVisitor(SemanticContext sc) {
		super(sc);
		
		lenseTypeSystem = LenseTypeSystem.getInstance();
		
		ANY = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Any", 0).get();
		VOID = (LenseTypeDefinition) sc.resolveTypeForName("lense.core.lang.Void", 0).get();
		// NOTHING = (LenseTypeDefinition)
		// sc.resolveTypeForName("lense.core.lang.Nothing", 0).get();
	}
	
	public SemanticVisitor(SemanticContext sc, Map<TypeVariable, List<TypeDefinition>> enhancements) {
		this(sc);
		this.enhancements = enhancements;
	}

	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		return Optional.ofNullable(currentType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {

		if (this.currentType != null && !this.currentType.getKind().isEnhancement()) {

			if (!expectedMethods.isEmpty()) {

				if (!this.currentType.isAbstract() && !this.currentType.isNative()) {
					for (List<Method> list : expectedMethods.values()) {
						for (Method m : list) {
							if (!m.isNative() && !m.isPropertyBridge() && m.isAbstract()) {
								throw new CompilationError(this.currentType.getName()
										+ " is not abstract and does not implement abstract method " + m.getName()
										+ " in " + m.getDeclaringType().getName());
							}
						}
					}

				}

			}

			if (!currentType.hasConstructor()) {
				// if no constructor exists, add a default one
				Constructor ctr = new Constructor("constructor", Collections.emptyList(), false, Visibility.Public);
				currentType.addConstructor(ctr);
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
							if (m.isAbstract() && !m.isPropertyBridge()) {
								Collection<Method> implemented = currentType.getMethodsByName(m.getName());
								if (!implemented.stream().anyMatch(i -> lenseTypeSystem.isMethodImplementedBy(m, i))) {
									throw new CompilationError(
											currentType.getSimpleName() + " is not abstract and method " + m.toString()
											+ " on " + st.getName() + "  is not implemented");
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
		if (node instanceof AssertNode) {
			AssertNode r = (AssertNode) node;
			ExpressionNode val = (ExpressionNode) r.getFirstChild();

			if (val instanceof PreBooleanUnaryExpression) {
				PreBooleanUnaryExpression exp = (PreBooleanUnaryExpression)val;
				
				if (exp.getOperation() == BooleanOperation.LogicNegate) {
					
	
					exp.getParent().replace(val, exp.getFirstChild());
					
					r.setReferenceValue(false);
				}
			}

		} else if (node instanceof ContinueNode) {
			// verify is use inside a loop
			if (!isUsedInLoop(node)) {
				throw new CompilationError(node, "Cannot use continue directive outside a loop");
			}

		} else if (node instanceof BreakNode) {
			// verify is use inside a loop
			if (!isUsedInLoop(node)) {
				throw new CompilationError(node, "Cannot use break directive outside a loop");
			}

		} else if (node instanceof ConstructorDeclarationNode) {

			ConstructorDeclarationNode constructorDeclarationNode = (ConstructorDeclarationNode) node;

			// defaults
			if (constructorDeclarationNode.getVisibility() == null) {
				constructorDeclarationNode.setVisibility(Visibility.Private); // TODO set the class visibility level
			}

			constructorDeclarationNode
			.setReturnType(new TypeNode(this.getSemanticContext().currentScope().getCurrentType()));

			// define variable in the method scope. the current scope is block
			this.getSemanticContext().currentScope().defineVariable("@returnOfMethod",
					this.getSemanticContext().currentScope().getCurrentType(), node);

		} else if (node instanceof MethodDeclarationNode) {

			MethodDeclarationNode m = (MethodDeclarationNode) node;

			// defaults
			if (m.getVisibility() == null) {
				m.setVisibility(Visibility.Private);
			}

			// auto-abstract if interface
			if (this.currentType != null && this.currentType.getKind() == LenseUnitKind.Interface && !m.isDefault()) {
				m.setAbstract(true);
			}

			// define variable in the method scope. the current scope is block
			this.getSemanticContext().currentScope().defineVariable("@returnOfMethod", LenseTypeSystem.Nothing(), node);

			// verify overriding
			if (this.currentType != null) {
				if (!m.isNative() && m.isAbstract() && !this.currentType.isAbstract()) {
					throw new CompilationError(node,
							m.getName() + " is abstract but " + this.currentType.getName() + " is not abstract");
				}

				List<Method> superMethods = expectedMethods.get(m.getName());

				if (superMethods == null || superMethods.isEmpty()) {
					// no supper method exists, so this method cannot declare override
					if (m.isOverride()) {
						throw new CompilationError(node, "The method " + m.getName() + " of type "
								+ this.currentType.getName() + " must override or implement a supertype method");
					}
				} else {
					// a supper method exists.

					// match signatures
					for (Method superMethod : superMethods) {

						if (superMethod.getParameters().size() == m.getParameters().getChildren().size()) {

							boolean analiseInheritance = true;
							if (!superMethod.getParameters().isEmpty()) {
								Iterator<CallableMemberMember<Method>> ita = superMethod.getParameters().iterator();
								Iterator<AstNode> itb = m.getParameters().getChildren().iterator();

								while (ita.hasNext()) {
									MethodParameter superParameter = (MethodParameter) ita.next();
									FormalParameterNode thisParameter = (FormalParameterNode) itb.next();

									 boolean matches =  this.lenseTypeSystem.isAssignableTo(thisParameter.getTypeVariable(), superParameter.getType().getUpperBound()) 
									 && this.lenseTypeSystem.isAssignableTo(superParameter.getType().getLowerBound(), thisParameter.getTypeVariable());
									
									if (!matches) {
										analiseInheritance = false;
										break;
									}
								}
							}

							if (analiseInheritance) {

								if (!superMethod.isAbstract() && !m.isOverride()) {
									throw new CompilationError(node,
											"The method " + m.getName() + " in type " + this.currentType.getName()
											+ " must declare override of a supertype method in "
											+ superMethod.getDeclaringType().getName());
								}

								if (!superMethod.isAbstract() && !superMethod.isDefault()) {
									throw new CompilationError(node,
											"The method " + m.getName() + " ib type " + this.currentType.getName()
											+ " cannot override a non default supertype method in "
											+ superMethod.getDeclaringType().getName());
								}

								m.setSuperMethod(superMethod);

								m.getMethod().setSuperMethod(superMethod);
								expectedMethods.remove(m.getName());
							}

						} // else, not the same number of parameters: is an overload.
					}
				}
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

					this.getSemanticContext().currentScope().defineVariable(var.getName(), type, node)
					.setInitialized(true);
				}
			}

			// define variable in the method scope. the current scope is block
			this.getSemanticContext().currentScope().defineVariable("@returnOfMethod",
					m.getParent().getType().getTypeParameter(), node);

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

					this.getSemanticContext().currentScope().defineVariable(var.getName(), type, node)
					.setInitialized(true);
				}
			}

			TypeVariable type = m.getParent().getType().getTypeVariable();
			if (type == null) {
				type = m.getParent().getType().getTypeParameter();
			}

			this.getSemanticContext().currentScope().defineVariable(m.getValueVariableName(), type, node)
			.setInitialized(true);
		} else if (node instanceof ClassTypeNode) {
			ClassTypeNode t = (ClassTypeNode) node;

			int genericParametersCount = t.getGenerics() == null ? 0 : t.getGenerics().getChildren().size();

			Optional<TypeVariable> maybeMyType = this.getSemanticContext().resolveTypeForName(t.getName(),
					genericParametersCount);

			TypeNode superTypeNode = t.getSuperType();
			
			LenseTypeDefinition myType;
			if (maybeMyType.isPresent()) {
				myType = (LenseTypeDefinition) maybeMyType.get();
			} else {

				List<TypeVariable> genericVariables = new ArrayList<>(genericParametersCount);

				if (genericParametersCount > 0) {

					for (AstNode a : t.getGenerics().getChildren()) {
						GenericTypeParameterNode g = (GenericTypeParameterNode) a;

						TypeNode tn = g.getTypeNode();

						RangeTypeVariable r = new RangeTypeVariable(tn.getName(), g.getVariance(),
								LenseTypeSystem.Any(), LenseTypeSystem.Nothing());
						genericVariables.add(r);

						this.getSemanticContext().currentScope().defineTypeVariable(tn.getName(), r, node);
					}

				}
				

				
				myType = new LenseTypeDefinition(t.getName(), t.getKind(), ANY, genericVariables);
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

			TypeDefinition superType = ANY;
			if (superTypeNode != null) {

				superType = this.getSemanticContext().typeForName(superTypeNode).getTypeDefinition();

				if (superType.isGeneric()) {

					if (t.getKind().isEnhancement()) {
						
						List<TypeVariable> params = new ArrayList<>(superTypeNode.getChildren().size());
						for (AstNode n : superTypeNode.getChildren()) {
							if (n instanceof GenericTypeParameterNode) {
								GenericTypeParameterNode g = (GenericTypeParameterNode)n;
								
								params.add(this.getSemanticContext().typeForName(g.getTypeNode()));
								
								
							} else {
								throw new UnsupportedOperationException();
							}
						}
						
						superType = LenseTypeSystem.specify(superType, params);
						
					} else {
						for (AstNode n : superTypeNode.getChildren()) {
							if (n instanceof GenericTypeParameterNode) {
								throw new UnsupportedOperationException();
							} else {
								TypeNode tn = (TypeNode) n;
								TypeDefinition rawInterfaceType = this.getSemanticContext().typeForName(tn)
										.getTypeDefinition();
								TypeDefinition interfaceType = rawInterfaceType;
								if (rawInterfaceType.isGeneric()) {
									TypeVariable[] parameters = new TypeVariable[tn.getChildren().size()];
									int index = 0;
									for (AstNode a : tn.getChildren()) {
										GenericTypeParameterNode g = (GenericTypeParameterNode) a;
										TypeNode tt = g.getTypeNode();
										for (int i = 0; i < myType.getGenericParameters().size(); i++) {
											TypeVariable v = myType.getGenericParameters().get(i);
											if (v.getSymbol().get().equals(tt.getName())) {
												parameters[index] = new DeclaringTypeBoundedTypeVariable(myType, i,
														tt.getName(), g.getVariance());
											}
										}
										index++;
									}

									interfaceType = LenseTypeSystem.specify(rawInterfaceType, parameters);

								}

								tn.setTypeVariable(interfaceType);
								myType.addInterface(interfaceType);
							}

						}
					}
					

				}

				
				if (!t.getKind().isEnhancement() &&  superType.getKind() == LenseUnitKind.Interface && !lenseTypeSystem.isAny(superType)) {
					throw new CompilationError(node, t.getName() + " cannot extend interface " + superType.getName()
					+ ". Did you meant to use 'implements' instead of 'extends' ?.");
				}

				superTypeNode.setTypeVariable(superType);

			} else if (t.getKind().isEnhancement()){
				throw new CompilationError(node, t.getName() + " enhancement must define a type for extention");
				
			}

			if (superType.equals(myType)) {
				if (!myType.equals(ANY)) {
					throw new CompilationError(node, t.getName() + " cannot extend it self");
				}
			} else {
				myType.setSuperTypeDefinition(superType);

				if (superType == ANY) {
					// supertype is Any // TODO change on loaded
					superType.getAllMembers().stream().filter(m -> m.isMethod() && !m.isProperty()).peek(m -> {
						m.setAbstract(false);
						m.setDefault(!m.getName().equals("type"));
					}).forEach(m -> addExpected(m.getName(), ((Method) m)));
				} else {
					superType.getAllMembers().stream().filter(m -> m.isMethod() && !m.isProperty())
					.forEach(m -> addExpected(m.getName(), ((Method) m)));
				}
			}

			// algebric values
			if (t.isAlgebric()) {

				myType.setAlgebric(true);

				List<TypeDefinition> chidlValues = new ArrayList<>(t.getAlgebricChildren().getChildren().size());
				List<TypeDefinition> chidlTypes = new ArrayList<>(t.getAlgebricChildren().getChildren().size());

				for (AstNode n : t.getAlgebricChildren().getChildren()) {
					ChildTypeNode ctn = (ChildTypeNode) n;

					TypeDefinition childType = this.getSemanticContext()
							.resolveTypeForName(ctn.getType().getName(), ctn.getType().getTypeParametersCount()).get()
							.getTypeDefinition();

					if (childType.getTypeDefinition().getKind().isObject()) {
						chidlValues.add(childType);
					} else {
						chidlTypes.add(childType);
					}

					ctn.getType().setTypeVariable(childType);

				}

				myType.setCaseTypes(chidlTypes);
				myType.setCaseValues(chidlValues);
			}

			t.setTypeDefinition(myType);

			
			
			this.currentType = myType;
			
		

			if (t.getKind().isEnhancement()) {
				
				this.getSemanticContext().currentScope().defineVariable("this", superType, node).setInitialized(true);

			} else {
				this.getSemanticContext().currentScope().defineVariable("this", myType, node).setInitialized(true);

				this.getSemanticContext().currentScope().defineVariable("super", superType, node).setInitialized(true);

			}
			
			
			TreeTransverser.transverse(t, new StructureVisitor(myType, this.getSemanticContext()));

			if (t.getInterfaces() != null) {
				for (AstNode n : t.getInterfaces().getChildren()) {
					// generifyInterfaceType(myType, myGenericTypes, (TypeNode) n);

					TypeDefinition interfaceType = specifySuperInterface(myType, myType.getGenericParameters(),
							(TypeNode) n);

					myType.addInterface(interfaceType);
					interfaceType.getAllMembers().stream().filter(m -> m.isMethod() && !m.isProperty())
					.forEach(m -> addExpected(m.getName(), ((Method) m)));

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

			TreeTransverser.transverse(n.getContainer(), new SemanticVisitor(this.getSemanticContext()));

			TypeVariable containerTypeVariable = n.getContainer().getTypeVariable();

			TypeVariable typeVariable = containerTypeVariable.getGenericParameters().get(0);

			n.getVariableDeclarationNode().setTypeNode(new TypeNode(typeVariable));

			VariableInfo iterationVariable = this.getSemanticContext().currentScope()
					.defineVariable(n.getVariableDeclarationNode().getName(), typeVariable, n);

			iterationVariable.setInitialized(true);

			if (n.getContainer() instanceof RangeNode) {
				RangeNode range = (RangeNode) n.getContainer();

				if (lenseTypeSystem.isAssignableTo(iterationVariable.getTypeVariable(),
						LenseTypeSystem.Natural())) { // TODO change to .Number()
					// is a number
					// try to determine limits

					if (range.getStart() instanceof NumericValue) {
						iterationVariable.setMininumValue(((NumericValue) range.getStart()).getValue());
					}

					if (range.getEnd() instanceof NumericValue) {
						iterationVariable.setMaximumValue(((NumericValue) range.getEnd()).getValue());
					}

				}

			}

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

			List<TypeVariable> parameters = new ArrayList<>();
			if (assignmentType.getTypeDefinition().getName().equals("lense.core.lang.Function")) {
				parameters = assignmentType.getTypeDefinition().getGenericParameters();
			}

			int index = 1;
			for (AstNode p : n.getParameters().getChildren()) {

				FormalParameterNode d = ((FormalParameterNode) p);
				String name = d.getName();
				TypeVariable td = d.getTypeVariable();
				if (td == null) {
					td = parameters.get(index); // TODO Type inference to
					// name resolution
					d.setTypeNode(new TypeNode(td));
				}

				this.getSemanticContext().currentScope().defineVariable(name, td, node).setInitialized(true);
				index++;
			}
		}

		return VisitorNext.Children;
	}

	private boolean isUsedInLoop(AstNode node) {

		if (node == null) {
			return false;
		}

		AstNode parent = node.getParent();
		if (parent instanceof ForEachNode || parent instanceof WhileNode) {
			return false;
		} else if (parent instanceof PropertyDeclarationNode || parent instanceof MethodDeclarationNode
				|| parent instanceof DecisionNode) {
			return false;
		} else {
			return isUsedInLoop(parent);
		}
	}

	private void addExpected(String name, Method method) {

		if (method.isPropertyBridge()) {
			return;
		}
		List<Method> list = expectedMethods.get(name);

		if (list == null) {
			list = new ArrayList<>(1);
			expectedMethods.put(name, list);
		} else {
			if (list.contains(method)) {
				return;
			} else {
				Iterator<Method> it = list.iterator();

				boolean add = false;

				outter: while (it.hasNext()) {
					Method current = it.next();

					for (int i = 0; i < current.getParameters().size(); i++) {

						TypeVariable p = current.getParameters().get(i).getType();
						TypeVariable n = method.getParameters().get(i).getType();

						if (lenseTypeSystem.isAssignableTo(p, n)) {
							it.remove();
							add = true;
							continue outter;
						}
					}
				}

				if (add) {
					list.add(method);
					return;
				}
			}
		}

		list.add(method);
	}

	private TypeDefinition specifySuperInterface(LenseTypeDefinition declaringType,
			List<TypeVariable> implementationGenericTypes, TypeNode interfaceNode) {

		TypeDefinition rawInterfaceType = this.getSemanticContext().typeForName(interfaceNode).getTypeDefinition();

		if (rawInterfaceType.getGenericParameters().isEmpty()) {
			// no generics
			interfaceNode.setTypeVariable(rawInterfaceType);
			return rawInterfaceType;
		}

		TypeVariable[] parameters = new TypeVariable[interfaceNode.getTypeParametersCount()];
		int index = 0;
		for (AstNode a : interfaceNode.getChildren()) {
			// match the relevant generic types
			GenericTypeParameterNode g = (GenericTypeParameterNode) a;
			TypeNode generitcTypeParameter = g.getTypeNode();

			if (generitcTypeParameter.getTypeVariable() == null
					|| generitcTypeParameter.getTypeVariable().getTypeDefinition().equals(ANY)) {
				for (int i = 0; i < implementationGenericTypes.size(); i++) {
					TypeVariable v = implementationGenericTypes.get(i);
					if (v.getSymbol().get().equals(generitcTypeParameter.getName())) {

						parameters[index] = new DeclaringTypeBoundedTypeVariable(declaringType, i,
								generitcTypeParameter.getName(), g.getVariance());

					}
				}
			} else {
				if (generitcTypeParameter.getTypeParametersCount() > 0) {
					// Recursive call
					parameters[index] = specifySuperInterface(declaringType, implementationGenericTypes,
							generitcTypeParameter);

				} else {
					parameters[index] = generitcTypeParameter.getTypeVariable();
				}
			}

			index++;
		}

		LenseTypeDefinition interfaceType = LenseTypeSystem.specify(rawInterfaceType, parameters);
		interfaceNode.setTypeVariable(interfaceType);

		return interfaceType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doVisitAfterChildren(AstNode node) {
		if (node instanceof AssertNode) {
			AssertNode r = (AssertNode) node;
			ExpressionNode val = (ExpressionNode) r.getFirstChild();

			if (!(val instanceof ComparisonNode) && !(val instanceof InstanceOfNode)) {
				ComparisonNode n = new ComparisonNode(Operation.EqualTo);
				n.add(val);
				n.add(new BooleanValue(r.getReferenceValue()));

				r.replace(val, n);
			}

		} else if (node instanceof SwitchNode) {
			SwitchNode switchNode = (SwitchNode) node;

			TypeDefinition type = ensureNotFundamental(switchNode.getCandidate().getTypeVariable().getTypeDefinition());
			switchNode.getCandidate().setTypeVariable(type);

			if (type.isAlgebric()) {
				Set<TypeDefinition> set = new HashSet<>(
						switchNode.getCandidate().getTypeVariable().getTypeDefinition().getCaseValues());

				boolean defaultFound = false;
				for (AstNode op : switchNode.getOptions().getChildren()) {
					SwitchOption t = (SwitchOption) op;

					if (t.isDefault()) {
						defaultFound = true;
						continue;
					}

					if (t.getValue() instanceof BooleanValue) {
						set.remove(new FundamentalLenseTypeDefinition(
								"lense.core.lang." + ((BooleanValue) t.getValue()).getLiteralValue(),
								LenseUnitKind.Object, (LenseTypeDefinition) LenseTypeSystem.Boolean()));
					} else {
						set.remove(t.getValue().getTypeVariable().getTypeDefinition());
					}

				}

				if (!set.isEmpty() && !defaultFound) {
					// check if default option is present
					throw new CompilationError(node,
							"Not all cases where covered and no default case was declared. Cover all case values or declare a default case.");
				}
			} else {
				boolean defaultFound = switchNode.getOptions().getChildren().stream()
						.filter(c -> ((SwitchOption) c).isDefault()).findAny().isPresent();
				if (!defaultFound) {
					// check if default option is present
					throw new CompilationError(node,
							"Candidate type has no declared coverage and default case was not declared. Ddeclare a default case.");
				}
			}
		} else {
			LenseTypeSystem typeSystem = lenseTypeSystem;

			if (node instanceof ComparisonNode) {
				ComparisonNode n = (ComparisonNode) node;

				TypeDefinition comparable = LenseTypeSystem.Comparable();
				TypeVariable leftSide = ensureNotFundamental(n.getLeft().getTypeVariable());

				if (n.getOperation().dependsOnComparable() && !typeSystem.isAssignableTo(leftSide, comparable)) {
					throw new CompilationError(node, leftSide.getTypeDefinition().getName() + " is not Comparable");
				}
			} else if (node instanceof ConstructorDeclarationNode) {
				ConstructorDeclarationNode ctr = new ConstructorDeclarationNode();

				if (ctr.isImplicit()) {
					if (ctr.getParameters().getChildren().isEmpty()) {
						throw new CompilationError(node, "An implicit constructor must have one parameter");
					} else if (ctr.getParameters().getChildren().size() > 1) {
						throw new CompilationError(node, "An implicit constructor can only have one parameter");
					}
				}

				if (ctr.isPrimary() && ctr.getBlock() != null) {
					throw new CompilationError(node, "The primary constructor cannot declare a body");
				}
			} else if (node instanceof ClassBodyNode) {
				ClassBodyNode n = (ClassBodyNode) node;
				if (!currentType.hasConstructor() && !currentType.isAbstract()
						&& currentType.getKind() == LenseUnitKind.Class) {
					// if no constructor exists, add a default one
					currentType.addConstructor(
							new Constructor("constructor", Collections.emptyList(), false, Visibility.Public));

					ConstructorDeclarationNode c = new ConstructorDeclarationNode();
					c.setReturnType(new TypeNode(currentType));
					c.setPrimary(true);
					c.setImplicit(false);
					c.setVisibility(Visibility.Public);
					n.add(c);
				}

			} else if (node instanceof TypeNode) {
				TypeNode t = (TypeNode) node;
				if (t.needsInference()) {
					return;
				}
				resolveTypeDefinition(t, Variance.Invariant); // TODO read parent nodes to determine variance
			} else if (node instanceof LiteralSequenceInstanceCreation) {
				LiteralSequenceInstanceCreation literal = (LiteralSequenceInstanceCreation) node;

				TypeDefinition maxType = ((TypedNode) literal.getArguments().getFirst().getFirstChild())
						.getTypeVariable().getTypeDefinition();

				maxType = this.getSemanticContext()
						.resolveTypeForName(maxType.getName(), maxType.getGenericParameters().size()).get()
						.getTypeDefinition();

				boolean isMaybe = typeSystem.isMaybe(maxType);

				for (int i = 1; i < literal.getArguments().getChildren().size(); i++) {
					AstNode n = literal.getArguments().getChildren().get(i).getFirstChild();
					TypedNode t = (TypedNode) n;
					TypeDefinition nextType = t.getTypeVariable().getTypeDefinition();
					nextType = this.getSemanticContext()
							.resolveTypeForName(nextType.getName(), nextType.getGenericParameters().size()).get()
							.getTypeDefinition();

					if (!nextType.equals(maxType)) {
						if (typeSystem.isPromotableTo(maxType, nextType)) {
							maxType = nextType;
						} else if (!typeSystem.isPromotableTo(nextType, maxType)) {

							isMaybe = isMaybe || typeSystem.isMaybe(nextType);

							if (!isMaybe) {
								// TODO incompatible types in the same array
								throw new CompilationError(node, "Heterogeneous Sequence");
							}

						}
					}
				}

				if (isMaybe) {

					TypeDefinition innerType = maxType;
					TypeDefinition maybeType = this.getSemanticContext()
							.resolveTypeForName(LenseTypeSystem.Maybe().getName(), 1).get().getTypeDefinition();
					maxType = LenseTypeSystem.specify(maybeType, maxType);

					TypeDefinition someType = this.getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1)
							.get().getTypeDefinition();

					TypeVariable innerTypeVar = innerType;

					ListIterator<AstNode> lstIterator = literal.getArguments().listIterator();

					while (lstIterator.hasNext()) {
						AstNode n = lstIterator.next().getFirstChild();
						TypeDefinition type = ((TypedNode) n).getTypeVariable().getTypeDefinition();

						if (typeSystem.isMaybe(type)) {
							continue;
						}
						if (!typeSystem.isAssignableTo(type, innerType)) {
							if (typeSystem.isPromotableTo(type, innerType)) {

								Optional<Constructor> op = innerType.getConstructorByParameters(Visibility.Public,
										new ConstructorParameter(type));

								NewInstanceCreationNode cn = NewInstanceCreationNode.of(innerTypeVar, op.get(), n);
								cn.getCreationParameters().getTypeParametersListNode()
								.add(new GenericTypeParameterNode(new TypeNode(innerTypeVar)));

								lstIterator.set(cn);

							} else {
								throw new CompilationError(node,
										"Heterogeneous Sequence. Cannot promote " + type + " to " + innerType);
							}
						}
					}

					TypeVariable maxTypeDef = maxType;

					literal.getCreationParameters().getTypeParametersListNode()
					.add(new GenericTypeParameterNode(new TypeNode(maxTypeDef)));

					lstIterator = literal.getArguments().listIterator();

					while (lstIterator.hasNext()) {
						AstNode n = lstIterator.next().getFirstChild();
						TypeDefinition type = ((TypedNode) n).getTypeVariable().getTypeDefinition();
						if (!typeSystem.isMaybe(type)) {

							Optional<Constructor> op = someType.getConstructorByParameters(Visibility.Public,
									new ConstructorParameter(type));
							TypeVariable someTypeSpec = LenseTypeSystem.specify(someType, type);

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(someTypeSpec, op.get(), n);
							cn.getCreationParameters().getTypeParametersListNode()
							.add(new GenericTypeParameterNode(new TypeNode(someTypeSpec)));

							lstIterator.set(cn);

						}
					}
				} else {
					TypeVariable maxTypeDef = maxType;

					literal.getCreationParameters().getTypeParametersListNode()
					.add(new GenericTypeParameterNode(new TypeNode(maxTypeDef)));

					ListIterator<AstNode> lstIterator = literal.getArguments().listIterator();

					while (lstIterator.hasNext()) {
						AstNode n = lstIterator.next().getFirstChild();
						TypeDefinition type = ((TypedNode) n).getTypeVariable().getTypeDefinition();
						if (!typeSystem.isAssignableTo(type, maxType)) {
							if (typeSystem.isPromotableTo(type, maxType)) {

								Optional<Constructor> op = maxType.getConstructorByParameters(Visibility.Public,
										new ConstructorParameter(type));

								NewInstanceCreationNode cn = NewInstanceCreationNode.of(maxTypeDef, op.get(), n);
								cn.getCreationParameters().getTypeParametersListNode()
								.add(new GenericTypeParameterNode(new TypeNode(maxTypeDef)));

								lstIterator.set(cn);

							} else {
								throw new CompilationError(node,
										"Heterogeneous Sequence. Cannot promote " + type + " to " + maxType);
							}
						}
					}

				}

				Optional<TypeVariable> sequenceType = this.getSemanticContext().resolveTypeForName(
						LenseTypeSystem.Sequence().getName(), LenseTypeSystem.Sequence().getGenericParameters().size());

				TypeVariable seqType = LenseTypeSystem.specify(sequenceType.get(), maxType);

				literal.setTypeVariable(seqType);

				TypeParametersListNode typeParametersListNode = literal.getCreationParameters()
						.getTypeParametersListNode();

				typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(seqType)));

			} else if (node instanceof LiteralAssociationInstanceCreation) {
				LiteralAssociationInstanceCreation literal = (LiteralAssociationInstanceCreation) node;

				TypeDefinition keypair = ((TypedNode) literal.getArguments().getFirst().getFirstChild())
						.getTypeVariable().getTypeDefinition();
				TypeVariable keyType = keypair.getGenericParameters().get(0);
				TypeVariable valueType = keypair.getGenericParameters().get(1);

				literal.getCreationParameters().getTypeParametersListNode()
				.add(new GenericTypeParameterNode(new TypeNode(keyType)));
				literal.getCreationParameters().getTypeParametersListNode()
				.add(new GenericTypeParameterNode(new TypeNode(valueType)));

				for (ArgumentListItemNode a : literal.getArguments().getChildren(ArgumentListItemNode.class)) {
					NewInstanceCreationNode n = ((NewInstanceCreationNode) a.getFirstChild());

					TypeParametersListNode typeParametersListNode = n.getCreationParameters().getTypeParametersListNode();

					typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(keyType)));
					typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(valueType)));

					a.setExpectedType(LenseTypeSystem.specify(keypair, keyType, valueType));
				}

				literal.setTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Association(), keyType, valueType));

			} else if (node instanceof LiteralTupleInstanceCreation) {

				LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);

				TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0).getFirstChild();
				TypedNode nextTuple;
				if (tuple.getChildren().get(1).getChildren().size() == 2) {
					nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1).getFirstChild();
				} else {
					nextTuple = new TypeNode(LenseTypeSystem.Nothing());
				}

				LenseTypeDefinition tupleType = LenseTypeSystem.specify(LenseTypeSystem.Tuple(),
						value.getTypeVariable(), nextTuple.getTypeVariable());

				tuple.getCreationParameters().getTypeParametersListNode()
				.add(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable())));
				tuple.getCreationParameters().getTypeParametersListNode()
				.add(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));

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

				((LiteralTupleInstanceCreation) node).setTypeVariable(tupleType);
			} else if (node instanceof RangeNode) {
				RangeNode r = (RangeNode) node;

				TypeVariable left = ((TypedNode) r.getChildren().get(0)).getTypeVariable();
				TypeVariable right = ((TypedNode) r.getChildren().get(1)).getTypeVariable();

				TypeVariable finalType;
				if (left.equals(right)) {
					finalType = left;
				} else if (typeSystem.isPromotableTo(left, right)) {
					finalType = right;
				} else if (typeSystem.isPromotableTo(right, left)) {
					finalType = left;
				} else {
					throw new CompilationError(node, "Cannot create range from " + left + " to " + right);
				}

				r.setTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Progression(), finalType));

				ArgumentListItemNode arg = new ArgumentListItemNode(0, r.getEnd());
				arg.setExpectedType(r.getEnd().getTypeVariable());

				String name = "upTo";
				if (!r.isIncludeEnd()) {
					name = "upToExclusive";
				}
				

				MethodInvocationNode create = new MethodInvocationNode( r.getStart(),name, arg);
				create.setTypeVariable(LenseTypeSystem.specify(LenseTypeSystem.Progression(), r.getStart().getTypeVariable()));

				r.getParent().replace(r, create);

			} else if (node instanceof LiteralIntervalNode) {
				LiteralIntervalNode r = (LiteralIntervalNode) node;

				Optional<TypeVariable> oleft = Optional.ofNullable(r.getStart()).map(s -> s.getTypeVariable());
				Optional<TypeVariable> oright = Optional.ofNullable(r.getEnd()).map(s -> s.getTypeVariable());

				TypeVariable finalType = null;
				if (oleft.isPresent() && oright.isPresent()) {
					TypeVariable left = oleft.get();
					TypeVariable right = oright.get();

					TypeDefinition leftDef = this.getSemanticContext()
							.resolveTypeForName(left.getTypeDefinition().getName(), left.getGenericParameters().size())
							.get().getTypeDefinition();
					TypeDefinition rightDef = this.getSemanticContext()
							.resolveTypeForName(right.getTypeDefinition().getName(),
									right.getGenericParameters().size())
							.get().getTypeDefinition();

					if (left.equals(right)) {
						finalType = left;
					} else if (typeSystem.isPromotableTo(leftDef, rightDef)) {
						finalType = right;

						// cast left to right

						Optional<Constructor> op = rightDef.getConstructorByParameters(Visibility.Public,
								new ConstructorParameter(left));

						NewInstanceCreationNode cast = NewInstanceCreationNode.of(finalType, op.get(), r.getStart());
						cast.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(finalType)));

						r.replace(r.getStart(), cast);

					} else if (typeSystem.isPromotableTo(rightDef, leftDef)) {
						finalType = left;

						// cast right to left

						Optional<Constructor> op = leftDef.getConstructorByParameters(Visibility.Public,
								new ConstructorParameter(right));

						NewInstanceCreationNode cast = NewInstanceCreationNode.of(finalType, op.get(), r.getEnd());
						cast.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(finalType)));

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

				TypeVariable type = this.getSemanticContext().resolveTypeForName("lense.core.math.Interval", 1).get();

				r.setTypeVariable(LenseTypeSystem.specify(type, finalType));

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

				n.setTypeVariable(funtionType);

			} else if (node instanceof BooleanOperatorNode) {
				BooleanOperatorNode b = (BooleanOperatorNode) node;

				Optional<ArithmeticOperation> equivalent = b.getOperation().equivalentArithmeticOperation();

				if (equivalent.isPresent()) {
					promoteArithmeticOperatorToMethodCall(b, b.getLeft(), b.getRight(), equivalent.get());
				}

			} else if (node instanceof ArithmeticNode) {
				ArithmeticNode n = (ArithmeticNode) node;

				promoteArithmeticOperatorToMethodCall(n, n.getLeft(), n.getRight(), n.getOperation());

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
				MethodInvocationNode method = new MethodInvocationNode(list.get(), ensureExpression(node.getChildren().get(0)));
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

				if (p.getOperation().equals(UnitaryOperation.Positive)) { /* +a */
					// +a is a no-op. replace the node by its content
					node.getParent().replace(node, node.getChildren().get(0));

				} else if (p.getOperation().equals(UnitaryOperation.Decrement)
						|| p.getOperation().equals(UnitaryOperation.Increment)) {
					String methodName;
					if (p.getOperation().equals(UnitaryOperation.Decrement)) { /* --a */
						methodName = "predecessor";
					} else if (p.getOperation().equals(UnitaryOperation.Increment)) { /* ++a */
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
					MethodInvocationNode method = new MethodInvocationNode(list.get(), ensureExpression(node.getChildren().get(0)));
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
				} else { /* -a , ~a */

					String methodName = p.getOperation().getArithmeticOperation().equivalentMethod();
					Optional<Method> list = type.getMethodsByName(methodName).stream()
							.filter(md -> md.getParameters().size() == 0).findAny();

					if (!list.isPresent()) {
						throw new CompilationError(node,
								"The method " + methodName + "() is undefined for TypeDefinition " + type);
					}

					// replace by a method invocation
					MethodInvocationNode method = new MethodInvocationNode(list.get(), ensureExpression(node.getChildren().get(0)));

					method.setTypeVariable(list.get().getReturningType());

					node.getParent().replace(node, method);
				}

			} else if (node instanceof PrimitiveBooleanValue) {
				// no-op
			} else if (node instanceof LiteralExpressionNode) {
				LiteralExpressionNode n = (LiteralExpressionNode) node;

				Optional<TypeVariable> resolvedType = this.getSemanticContext()
						.resolveTypeForName(n.getTypeVariable().getTypeDefinition().getName(), 0);

				n.setTypeVariable(resolvedType.get());

			} else if (node instanceof PreBooleanUnaryExpression) {
				PreBooleanUnaryExpression p = (PreBooleanUnaryExpression) node;

				final TypeDefinition type = ((TypedNode) p.getChildren().get(0)).getTypeVariable().getTypeDefinition();
				
				
				String methodName;
				if (p.getOperation().equals(BooleanOperation.BitNegate)) { /* ~a */
					// TODO verify operator interface Binary
					if (LenseTypeSystem.Boolean().equals(type)) {
						methodName = ArithmeticOperation.Complement.equivalentMethod();
					} else {
						throw new CompilationError(node,
								"Operator ~ can only be applied to Boolean instances ( found " + type.getName() + ")");
					}
				} else if (p.getOperation().equals(BooleanOperation.LogicNegate)) { /* !a */
					if (lenseTypeSystem.isBoolean(type)) {
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
				ExpressionNode expr = ensureExpression(node.getChildren().get(0));

				MethodInvocationNode method = new MethodInvocationNode(list.get(), expr);

				method.setTypeVariable(list.get().getReturningType());

				node.getParent().replace(node, method);

			} else if (node instanceof AssignmentNode) {
				AssignmentNode n = (AssignmentNode) node;

				TypeVariable left = n.getLeft().getTypeVariable();
				TypeVariable right = n.getRight().getTypeVariable();

				if (!typeSystem.isAssignableTo(right, left)) {

					if (!typeSystem.isPromotableTo(right, left)) {
						if (left.getTypeDefinition().getName().equals(LenseTypeSystem.Maybe().getName())) {
							// promotable to maybe
							if (!typeSystem.isPromotableTo(right,
									left.getTypeDefinition().getGenericParameters().get(0))) {
								throw new CompilationError(node, right + " is not assignable to " + left);
							}

							TypeDefinition someTpe = this.getSemanticContext()
									.resolveTypeForName("lense.core.lang.Some", 1).get().getTypeDefinition();

							someTpe = LenseTypeSystem.specify(someTpe, right);

							Optional<Constructor> op = someTpe.getConstructorByPromotableParameters(Visibility.Public,
									new ConstructorParameter(right));

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(someTpe, op.get(), n.getRight());
							cn.getCreationParameters().getTypeParametersListNode()
							.add(new GenericTypeParameterNode(new TypeNode(right)));

							n.replace((AstNode) n.getRight(), cn);
						} else {
							throw new CompilationError(node, right + " is not assignable to " + left);
						}

					} else {
						// TODO change to promote node, promotion is implicit
						// constructor based
						Optional<Constructor> op = left.getTypeDefinition()
								.getConstructorByParameters(Visibility.Public, new ConstructorParameter(right));

						NewInstanceCreationNode cn = NewInstanceCreationNode.of(left, op.get(), n.getRight());
						cn.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(left)));

						n.replace((AstNode) n.getRight(), cn);
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

						if (info == null) {
							throw new CompilationError(node, "Variable or field "
									+ ((FieldOrPropertyAccessNode) n.getLeft()).getName() + " is not defined");
						}
						if (info.isImutable() && info.isInitialized()) {

							AstNode parent = ((LenseAstNode) n.getLeft()).getParent().getParent().getParent();
							if (!(parent instanceof ConstructorDeclarationNode)) {
								throw new CompilationError(node,
										"Cannot modify the value of an imutable variable or field (" + info.getName()
										+ ")");
							}

						}
						info.setInitialized(true);
					} else {
						// property
						Optional<Property> property;
						if (fp.getPrimary() == null) {
							property = this.currentType.getPropertyByName(fp.getName());

						} else {
							property = ((TypedNode) fp.getPrimary()).getTypeVariable().getTypeDefinition()
									.getPropertyByName(fp.getName());

						}

						if (property.isPresent()) {

							if (!property.get().canWrite()) {
								throw new CompilationError(node,
										"Property " + ((FieldOrPropertyAccessNode) n.getLeft()).getName()
										+ " is read only and it cannot be asigned to");
							}

						} else {
							throw new CompilationError(node, "Property "
									+ ((FieldOrPropertyAccessNode) n.getLeft()).getName() + " is not defined in type "
									+ ((TypedNode) fp.getPrimary()).getTypeVariable().getTypeDefinition().getName());
						}
					}

				}

			} else if (node instanceof TernaryConditionalExpressionNode) {
				TernaryConditionalExpressionNode ternary = (TernaryConditionalExpressionNode) node;

				TypeVariable type = typeSystem.unionOf(ternary.getThenExpression().getTypeVariable(),
						ternary.getElseExpression().getTypeVariable());

				if (type instanceof UnionType) {
					UnionType unionType = (UnionType) type;

					if (typeSystem.isAssignableTo(unionType.getLeft(), unionType.getRight())) {
						type = unionType.getRight(); // TODO promote side
					} else if (typeSystem.isAssignableTo(unionType.getRight(), unionType.getLeft())) {
						type = unionType.getLeft(); // TODO promote side
					} else if (typeSystem.isPromotableTo(unionType.getLeft(), unionType.getRight())) {
						type = unionType.getRight(); // TODO promote side
					} else if (typeSystem.isPromotableTo(unionType.getRight(), unionType.getLeft())) {
						type = unionType.getLeft(); // TODO promote side
					}

				}
				ternary.setTypeVariable(type);
			} else if (node instanceof FormalParameterNode) {
				FormalParameterNode formal = ((FormalParameterNode) node);

				try {
					this.getSemanticContext().currentScope().defineVariable(formal.getName(), formal.getTypeVariable(),
							node);
				} catch (TypeAlreadyDefinedException e) {
					e.printStackTrace();
				}
			} else if (node instanceof ScopedVariableDefinitionNode) {
				ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode) node;

				TypedNode init = variableDeclaration.getInitializer();

				TypeVariable type = variableDeclaration.getTypeVariable();

				if (variableDeclaration.getTypeNode().needsInference()) {
					if (init != null) {
						type = init.getTypeVariable();
						variableDeclaration.getTypeNode().setTypeVariable(type);
					} else {
						throw new CompilationError(node, "Variable Type cannot be infered");
					}
				}

				VariableInfo info = this.getSemanticContext().currentScope()
						.searchVariable(variableDeclaration.getName());

				if (info == null) {
					try {
						info = this.getSemanticContext().currentScope().defineVariable(variableDeclaration.getName(),
								type, node);
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

					if (!typeSystem.isAssignableTo(right, type)) {
						if (typeSystem.isPromotableTo(right, type)) {
							// TODO use promote node
							Optional<Constructor> op = type.getTypeDefinition()
									.getConstructorByParameters(Visibility.Public, new ConstructorParameter(right));
							// TODO analyze literals for simplification without constructor

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(type, op.get(),
									variableDeclaration.getInitializer());

							if (!type.getGenericParameters().isEmpty()) {

								for (TypeVariable variable : type.getGenericParameters()) {
									cn.getCreationParameters().getTypeParametersListNode()
									.add(new GenericTypeParameterNode(new TypeNode(variable)));
								}

							}

							variableDeclaration.setInitializer(cn);
						} else if (typeSystem.isTuple(type, 1)) { // TODO
							// better
							// polimorphism
							// for
							// promotion

							final LiteralTupleInstanceCreation m = new LiteralTupleInstanceCreation(
									variableDeclaration.getInitializer());

							m.getCreationParameters().getTypeParametersListNode()
							.add(new GenericTypeParameterNode(new TypeNode(right)));
							// m.getCreationParameters().getTypeParametersListNode().add(new
							// GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));

							variableDeclaration.setInitializer(m);
							m.setTypeVariable(type);
						} else if (typeSystem.isMaybe(type)) {

							TypeDefinition someType = getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1)
									.get().getTypeDefinition();

							LenseTypeDefinition someTypeOfRight = LenseTypeSystem.specify(someType, right);

							Optional<Constructor> op = someType.getMembers().stream()
									.filter( m -> m.isConstructor() && m.getVisibility() == Visibility.Public)
									.map( c -> (Constructor)c)
									.findAny();
									
							TypeVariable someTypeSpec = someTypeOfRight;

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(someTypeSpec, op.get(),
									(AstNode) init);

							cn.getCreationParameters().getTypeParametersListNode()
							.add(new GenericTypeParameterNode(new TypeNode(someTypeSpec)));

							node.replace((AstNode) init, cn);
						} else {
							throw new CompilationError(node,
									right + " is not assignable to variable '" + info.getName() + "' of type " + type);
						}
					}
				}

				if (node instanceof FieldDeclarationNode) {
					FieldDeclarationNode f = (FieldDeclarationNode) node;

					LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope()
							.getCurrentType();

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
					if (!typeSystem.isAssignableTo(expType, propType)) {
						if (!typeSystem.isPromotableTo(expType, propType)) {
							throw new CompilationError(node,
									expType + " is not assignable to " + propType + " in property " + p.getName());
						} else {
							Optional<Constructor> op = propType.getTypeDefinition()
									.getConstructorByParameters(Visibility.Public, new ConstructorParameter(expType));

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(propType, op.get(), exp);
							cn.getCreationParameters().getTypeParametersListNode()
							.add(new GenericTypeParameterNode(new TypeNode(propType)));

							p.replace(exp, cn);
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

				LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope()
						.getCurrentType();

				String typeName = p.getType().getName();
				VariableInfo genericParameter = this.getSemanticContext().currentScope().searchVariable(typeName);

				TypeVariable propertyType = p.getType().getTypeVariable();

				if (genericParameter != null && genericParameter.isTypeVariable()) {
					List<TypeVariable> parameters = currentType.getGenericParameters();
					Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);

					if (!opIndex.isPresent()) {
						throw new CompilationError(node,
								typeName + " is not a valid generic parameter for type " + currentType.getName());
					}

					int index = opIndex.get();

					propertyType = new DeclaringTypeBoundedTypeVariable(currentType, index, typeName,
							parameters.get(index).getVariance());

				}

				if (p.isIndexed()) {

					lense.compiler.type.variable.TypeVariable[] params = new lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode) p)
					                                                                                                   .getIndexes().getChildren().size()];
					int i = 0;
					for (AstNode n : ((IndexerPropertyDeclarationNode) p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeParameter();
						// this.getSemanticContext().currentScope().defineTypeVariable(var.getName(),
						// type, p).setInitialized(true);
					}

					IndexerProperty indexer = currentType.addIndexer(propertyType, p.getAcessor() != null,
							p.getModifier() != null, params);

					ArrayList<TypeVariable> listParams = new ArrayList<>(Arrays.asList(params));

					// if (indexer.canWrite()) {
					// listParams.add(propertyType);
					// removeExpected("set", listParams);
					// }
					//
					// if (indexer.canRead()) {
					// removeExpected("get", listParams);
					// }
					//

				} else {
					Property property = currentType.addProperty(p.getName(), propertyType, p.getAcessor() != null,
							p.getModifier() != null);

					// if (property.canWrite()) {
					// removeExpected("set" + p.getName(), new
					// ArrayList<>(Arrays.asList(propertyType)));
					// }
					//
					// if (property.canRead()) {
					// removeExpected("get"+ p.getName() , Collections.emptyList());
					// }
				}

			} else if (node instanceof IndexedAccessNode) {
				IndexedAccessNode m = (IndexedAccessNode) node;

				TypedNode a = (TypedNode) m.getAccess();

				TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();

				TypeDefinition methodOwnerType = currentType;
				if (a != null) {
					if (a.getTypeVariable().isSingleType()) {
						methodOwnerType = a.getTypeVariable().getTypeDefinition();
					} else {
						throw new UnsupportedOperationException();
					}

				}

				if (methodOwnerType.getName().equals("lense.core.collections.Tuple")) {
					// only one index is allowed
					if (m.getArguments().getChildren().size() != 1) {
						throw new CompilationError(node, "Tuples only accept one index");
					}

					ExpressionNode indexArgument = (ExpressionNode) m.getArguments().getFirst().getFirstChild();
					Optional<Integer> index = asConstantNumber(indexArgument);
					if (index.isPresent()) {

						// Optional<Method> tail = methodOwnerType.getMethodsByName("tail").stream()
						// .filter(md -> md.getParameters().size() == 0).findAny();
						//
						// Optional<Method> head = methodOwnerType.getMethodsByName("head").stream()
						// .filter(md -> md.getParameters().size() == 0).findAny();

						int max = countTupleSize(methodOwnerType);

						Optional<Method> headMethod = LenseTypeSystem.Tuple().getMethodBySignature(new MethodSignature("head"));
						Optional<Method> tailMethod = LenseTypeSystem.Tuple().getMethodBySignature(new MethodSignature("tail"));
						
						if (index.get().intValue() == 0) {
							MethodInvocationNode invoke = new MethodInvocationNode(headMethod.get(), ensureExpression(m.getAccess()));

							node.getParent().replace(node, invoke);
							invoke.setTypeVariable(methodOwnerType.getGenericParameters().get(0));
							return;
						} else if (index.get() < max) {
							MethodInvocationNode previous = new MethodInvocationNode(tailMethod.get(), ensureExpression(m.getAccess()));
							previous.setTypeVariable(methodOwnerType.getGenericParameters().get(1));

							for (int i = 0; i < index.get() - 1; i++) {
								MethodInvocationNode current = new MethodInvocationNode(tailMethod.get(), previous);
								current.setTypeVariable(
										previous.getTypeVariable().getGenericParameters().get(1).getUpperBound());
								previous = current;
							}

							TypeVariable upperbound = previous.getTypeVariable().getGenericParameters().get(0)
									.getUpperBound();

							MethodInvocationNode invoke = new MethodInvocationNode(headMethod.get(), previous);
							invoke.setTypeVariable(upperbound);

							CastNode cast = new CastNode(invoke, upperbound.getTypeDefinition());

							node.getParent().replace(node, cast);

							return;
						}

					}
				} else {

					TypeVariable[] signatureTypes = new TypeVariable[m.getArguments().getChildren().size()];

					int index = 0;
					for (AstNode n : m.getArguments().getChildren()) {
						ArgumentListItemNode arg = (ArgumentListItemNode) n;
						TypeVariable type = arg.getExpectedType();

						if (type == null) {
							type = ((TypedNode) arg.getFirstChild()).getTypeVariable();
							arg.setExpectedType(type);
						}
						signatureTypes[index++] = type;
					}

					Optional<IndexerProperty> indexer = methodOwnerType.getIndexerPropertyByTypeArray(signatureTypes);

					if (!indexer.isPresent()) {
						throw new CompilationError(node, "No indexer "
								+ Stream.of(signatureTypes).map(t -> t.toString()).collect(Collectors.joining(","))
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

					Optional<TypeVariable> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

					while (!maybeType.isPresent()) {
						qn = qn.getPrevious();
						if (qn != null) {
							maybeType = this.getSemanticContext().resolveTypeForName((qn).getName(), 0);
						} else {
							break;
						}
					}

					if (maybeType.isPresent()) {
						TypeDefinition def = maybeType.get().getTypeDefinition();

						fieldOwnerType = def;
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
							throw new CompilationError(
									((QualifiedNameNode) access).getName() + " variable is not defined");
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

					TypeVariable ownerType = this.getSemanticContext().currentScope().searchVariable("this")
							.getTypeVariable();

					Optional<TypedNode> typedNode = typeForName(access, ((IdentifierNode) access).getName());

					if (typedNode.isPresent()) {
						fieldOwnerType = typedNode.get().getTypeVariable();

						node.replace(access, (AstNode) typedNode.get());
					} else {
						fieldOwnerType = ownerType;
					}

				} else {
					throw new CompilationError(access.getClass() + " Not supported yet");
				}

				if (fieldOwnerType.equals(currentType)) {

					resolveFieldPropertyOrVariableName(node, m, currentType, fieldOwnerType, name);

				} else {

					TypeDefinition def = ensureNotFundamental(fieldOwnerType.getTypeDefinition());
					Optional<Field> field = def.getFieldByName(name);

					if (!field.isPresent()) {

						Optional<Property> property = def.getPropertyByName(name);

						if (!property.isPresent()) {
							if (!typeSystem.isAssignableTo(def, LenseTypeSystem.Maybe())) {
								throw new CompilationError(node,
										"No field or property '" + name + "' is defined in " + fieldOwnerType);
							}

							TypeDefinition innerType = def.getGenericParameters().get(0).getTypeDefinition();

							field = innerType.getFieldByName(name);

							if (!field.isPresent()) {
								throw new CompilationError(node,
										"No field or property " + name + " is defined in " + fieldOwnerType);
							}

							// transform to call inside the maybe using map
							TypeVariable finalType = LenseTypeSystem.specify(LenseTypeSystem.Maybe(),
									field.get().getReturningType());
							TypeDefinition mappingFunction = LenseTypeSystem.specify(LenseTypeSystem.Function(2),
									innerType, field.get().getReturningType());

							TypeVariable type = mappingFunction;
							NewInstanceCreationNode newObject = NewInstanceCreationNode.of(type);
							
							newObject.getCreationParameters().getTypeParametersListNode().add(new GenericTypeParameterNode(new TypeNode(type)));

							ArgumentListItemNode arg = new ArgumentListItemNode(0, newObject);
							arg.setExpectedType(newObject.getTypeVariable());

							// TODO this will resolve the correct m
							Optional<Method> method = finalType.getTypeDefinition().getMethodBySignature(new MethodSignature("map", new MethodParameter(newObject.getTypeVariable())));
							
							MethodInvocationNode transform = new MethodInvocationNode(method.get(), ensureExpression(m.getPrimary()),
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
						VariableInfo info = this.getSemanticContext().currentScope().searchVariable(id.getName());

						if (info == null) {
							// try field

							TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();

							Optional<Field> field = currentType.getFieldByName(id.getName());

							if (!field.isPresent()) {

								Optional<Property> property = currentType.getPropertyByName(id.getName());

								if (!property.isPresent()) {
									throw new CompilationError(id, id.getName() + " is not a variable or a field");
								} else {
									FieldOrPropertyAccessNode r = new FieldOrPropertyAccessNode(id.getName());
									r.setKind(FieldKind.PROPERTY);
									r.setType(property.get().getReturningType());
									it.set(new ArgumentListItemNode(it.nextIndex() - 1, r));
								}

							} else {
								FieldOrPropertyAccessNode r = new FieldOrPropertyAccessNode(id.getName());
								r.setKind(FieldKind.FIELD);
								r.setType(field.get().getReturningType());
								it.set(new ArgumentListItemNode(it.nextIndex() - 1, r));
							}

						} else {
							VariableReadNode r = new VariableReadNode(id.getName(), info);
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

				TypeVariable methodOwnerType = this.getSemanticContext().currentScope().searchVariable("this")
						.getTypeVariable();
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
							throw new CompilationError(node, "Method " + signature + " is not defined in "
									+ methodOwnerType + " or its super classes");
						} else {

							List<CallableMemberMember<Method>> parameteres = method.get().getParameters();

							for (int i = 0; i < parameteres.size(); i++) {

								ArgumentListItemNode parent = ((ArgumentListItemNode) m.getCall().getArgumentListNode()
										.getChildren().get(i));
								ExpressionNode rightExpression = (ExpressionNode) parent.getFirstChild();

								promote(parent, rightExpression, parameteres.get(i).getType(),
										rightExpression.getTypeVariable());
							}

						}
					}

					m.setTypeVariable(method.get().getReturningType());


				} else if (access instanceof QualifiedNameNode) {
					QualifiedNameNode qn = ((QualifiedNameNode) access);

					Optional<TypeVariable> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

					while (!maybeType.isPresent()) {
						qn = qn.getPrevious();
						if (qn != null) {
							maybeType = this.getSemanticContext().resolveTypeForName((qn).getName(), 0);
						} else {
							break;
						}
					}

					if (maybeType.isPresent()) {
						TypeDefinition def = maybeType.get().getTypeDefinition();
						methodOwnerType = def;

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

							Optional<TypeVariable> obj = this.getSemanticContext().resolveTypeForName(varName, 0);

							if (obj.isPresent() && obj.get().getTypeDefinition().getKind() == LenseUnitKind.Object) {
								ObjectReadNode vnode = new ObjectReadNode(obj.get(), varName);

								access.getParent().replace(access, vnode);

								methodOwnerType = vnode.getTypeVariable();
							}

							// try property
							Optional<Property> property = this.getSemanticContext().currentScope().getCurrentType()
									.getPropertyByName(varName);

							if (!property.isPresent()) {

								// try a super property
								property = this.getSemanticContext().currentScope().getCurrentType()
										.getSuperDefinition().getPropertyByName(varName);

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
					
				} else if (access instanceof NameIdentifierNode) {

					TypeVariable ownerType = this.getSemanticContext().currentScope().searchVariable("this")
							.getTypeVariable();

					Optional<TypedNode> typedNode = typeForName(access, ((NameIdentifierNode) access).getName());

					if (typedNode.isPresent()) {
						methodOwnerType = typedNode.get().getTypeVariable();

						node.replace(access, (AstNode) typedNode.get());
					} else {
						methodOwnerType = ownerType;
					}

				} else if (access instanceof TypedNode) {
					methodOwnerType = ((TypedNode) access).getTypeVariable();
				} else {
					throw new CompilationError("Not supported yet");
				}

				TypeDefinition def = ensureNotFundamental(methodOwnerType.getTypeDefinition());

				// if (def.getAllMembers().isEmpty()) {
				// def = this.getSemanticContext().resolveTypeForName(def.getName(),
				// def.getGenericParameters().size()).get();
				// }

				MethodParameter[] parameters = asMethodParameters(m.getCall().getArgumentListNode());

				MethodSignature signature = new MethodSignature(name, parameters);

				Optional<Method> method = def.getMethodBySignature(signature);

				if (!method.isPresent()) {

					method = def.getMethodByPromotableSignature(signature);

					if (method.isPresent()) {
						
						applyMethodCall(node, m, method.get());
					} else {
						
						// search in enhancements
						
						if (!this.enhancements.isEmpty()) {
						
							List<Method> found = new LinkedList<>();
							
							for( Map.Entry<TypeVariable, List<TypeDefinition>> entry : this.enhancements.entrySet()) {
								
								if (lenseTypeSystem.isAssignableTo(methodOwnerType, entry.getKey())) {
									
									for (TypeDefinition enhancement : entry.getValue()) {
										method = enhancement.getMethodByPromotableSignature(signature);
										if (method.isPresent()) {
											found.add(method.get());
										}
									}

								}
							}
							
							if(found.size() == 1) {
								// set method return type
								
								applyMethodCall(node, m, found.get(0));
								
								m.setStaticInvocation(true);
								

								return;
							} else if (found.size() > 1) {
								throw new CompilationError(node, "More than one enhancement matches call to '" + name + "' in type '"
										+ def.getName() + "' with arguments " + Arrays.toString(parameters) + ". Please, desambiguate");
							}
						
						}
							
						throw new CompilationError(node, "There is no method named '" + name + "' in type '"
									+ def.getName() + "' with arguments " + Arrays.toString(parameters) + " nor an enchament matches");
			
					}

				} else {
					Method mthd = method.get();
					
					applyMethodCall(node, m, mthd);

				}

			} else if (node instanceof NewInstanceCreationNode) {

				if (node instanceof lense.compiler.ast.LiteralCreation) {
					return;
				}
				NewInstanceCreationNode n = (NewInstanceCreationNode) node;

				TypeDefinition def = n.getTypeNode().getTypeVariable().getTypeDefinition();

				ConstructorParameter[] parameters = n.getArguments() == null ? new ConstructorParameter[0]
						: asConstructorParameters(n.getArguments());

				Optional<Constructor> constructor = def.getConstructorByParameters(Visibility.Public, parameters);

				if (!constructor.isPresent()) {
					constructor = def.getConstructorByPromotableParameters(Visibility.Public, parameters);

					if (!constructor.isPresent()) {
						throw new CompilationError(n, "Constructor " + def.getName() + "("
								+ Stream.of(parameters).map(cp -> cp.toString()).collect(Collectors.joining(","))
								+ ") is not defined");
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

				// mark variable in the method scope.
				VariableInfo returnVariable = this.getSemanticContext().currentScope().searchVariable("@returnOfMethod");
				
			    TypeVariable union = typeSystem.unionOf(returnVariable.getTypeVariable(), n.getTypeVariable());
				
				returnVariable.setTypeVariable(union);

			} else if (node instanceof AccessorNode) {

				AccessorNode m = (AccessorNode) node;
				TypeVariable returnType = m.getParent().getType().getTypeVariable();

				if (!m.isAbstract() && !m.isImplicit()) {

					if (returnType != null && returnType.getTypeDefinition().equals(VOID)) {
						VariableInfo variable = this.getSemanticContext().currentScope()
								.searchVariable("@returnOfMethod");

						if (variable != null && !variable.getTypeVariable().equals(VOID)) {
							throw new CompilationError("Method " + m.getParent().getName() + " cannot return a value");
						}
					} else {
						VariableInfo variable = this.getSemanticContext().currentScope()
								.searchVariable("@returnOfMethod");

						if (!m.getParent().isNative() && variable == null) {

							LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext()
									.currentScope().getCurrentType();
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

					if (!m.getReturnType().needsInference()) {
						if (returnType.getTypeDefinition().equals(VOID)) {
							VariableInfo variable = this.getSemanticContext().currentScope()
									.searchVariable("@returnOfMethod");

							if (variable != null && !typeSystem.isAssignableTo(variable.getTypeVariable(), VOID)) {
								throw new CompilationError(node, "Method " + m.getName() + " cannot return a value");
							}
						} else {
							VariableInfo variable = this.getSemanticContext().currentScope()
									.searchVariable("@returnOfMethod");

							if (!m.isNative() && variable == null) {

								TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();
								if (currentType.getKind() == LenseUnitKind.Class) {
									throw new CompilationError(node,
											"Method " + m.getName() + " must return a result of " + returnType
											+ ". Found type " + currentType.getName());
								}
							}

							if (!typeSystem.isAssignableTo(variable.getTypeVariable(), returnType)) {

								if (!typeSystem.isPromotableTo(variable.getTypeVariable(), returnType)) {
									throw new CompilationError(node,
											variable.getTypeVariable() + " is not assignable to " + returnType
											+ " in the return of method " + m.getName());
								} else {
									// TODO promote

									ReturnNode rn = null;
									for (AstNode r : m.getBlock().getChildren()) {
										if (r instanceof ReturnNode) {
											rn = (ReturnNode) r;
											break;
										}
									}

									if (rn == null) {
										throw new CompilationError(node,
												variable.getTypeVariable() + " no return found");
									}

									Optional<Constructor> op = returnType.getTypeDefinition()
											.getConstructorByParameters(Visibility.Public,
													new ConstructorParameter(variable.getTypeVariable()));

									NewInstanceCreationNode cn = NewInstanceCreationNode.of(returnType, op.get(),
											rn.getChildren().get(0));
									cn.getCreationParameters().getTypeParametersListNode()
									.add(new GenericTypeParameterNode(new TypeNode(returnType)));

									ReturnNode nr = new ReturnNode();
									nr.add(cn);

									m.getBlock().replace(rn, nr);
								}
							}

						}

					}

				} else {
					if (this.getSemanticContext().currentScope().getCurrentType()
							.getKind() == LenseUnitKind.Interface) {
						m.setVisibility(Visibility.Public);
						m.setAbstract(true);
					}

				}

			} else if (node instanceof ClassTypeNode) {

				ClassTypeNode t = (ClassTypeNode) node;
				if (t.getInterfaces() != null) {

					for (AstNode n : t.getInterfaces().getChildren()) {
						TypeNode tn = (TypeNode) n;
						TypeDefinition typeVariable = ensureNotFundamental(tn.getTypeVariable().getTypeDefinition());
						if (typeVariable.getKind() != LenseUnitKind.Interface) {
							throw new CompilationError(t,
									t.getName() + " cannot implement " + typeVariable.getName() + " because "
											+ typeVariable.getName() + " it is a " + typeVariable.getKind()
											+ " and not an interface");
						}
					}
				}

				if (t.isAlgebric() && !t.isAbstract()) {
					throw new CompilationError(t, t.getName()
							+ " is algebric but is not marked abstract. Make it abstract or remove children types declarations.");
				}

			} else if (node instanceof ConditionalStatement) {

				if (!((ConditionalStatement) node).getCondition().getTypeVariable().getTypeDefinition()
						.equals(LenseTypeSystem.Boolean())) {
					throw new CompilationError(
							"Condition must be a Boolean value, found " + ((ConditionalStatement) node).getCondition()
							.getTypeVariable().getTypeDefinition().getName());
				}
			} else if (node instanceof ForEachNode) {
				ForEachNode n = (ForEachNode) node;

				if (!typeSystem.isAssignableTo(n.getContainer().getTypeVariable(), LenseTypeSystem.Iterable())) {

					//verify correct type loading 
					
					TypeDefinition def = n.getContainer().getTypeVariable().getTypeDefinition();
					TypeVariable g = this.getSemanticContext().resolveTypeForName(def.getName(), def.getGenericParameters().size()).get();
					
					if (!typeSystem.isAssignableTo(g, LenseTypeSystem.Iterable())) {
						throw new CompilationError(node, "Can only iterate over an instance of " + LenseTypeSystem.Iterable());
					}

				}

				if (!typeSystem.isAssignableTo(
						n.getContainer().getTypeVariable().getGenericParameters().get(0).getUpperBound(),
						n.getVariableDeclarationNode().getTypeVariable())) {
					throw new CompilationError(n.getVariableDeclarationNode().getTypeVariable().getSymbol()
							+ " is not contained in " + n.getContainer().getTypeVariable());
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
				if (!typeSystem.isAssignableTo(exceptionType, LenseTypeSystem.Exception())) {
					throw new CompilationError("No exception of TypeDefinition " + exceptionType.getSymbol()
					+ " can be thrown; an exception TypeDefinition must be a subclass of Exception");
				}

			} else if (node instanceof SwitchOption) {

				final SwitchOption s = (SwitchOption) node;
				if (!s.isDefault()) {
					boolean literal = s.getValue() instanceof LiteralExpressionNode;
					if (!literal) {
						boolean object = s.getValue() instanceof ObjectReadNode;

						if (!object) {
							throw new CompilationError("Switch case option value must be a constant");
						}
					}
				}

			}
		}
	}

	private void applyMethodCall(AstNode node, MethodInvocationNode m, Method mthd) {
		
		m.setTypeMember(mthd);
		m.setTypeVariable(mthd.getReturningType());

		List<CallableMemberMember<Method>> methodParameters = mthd.getParameters();
		if (methodParameters.size() != m.getCall().getArgumentListNode().getChildren().size()) {
			throw new CompilationError(m.getCall(), "Argument count does not match parameters count");
		}

		Map<String, List<ArgumentListItemNode>> freeArguments = new HashMap<>();

		for (int i = 0; i < methodParameters.size(); i++) {
			MethodParameter param = (MethodParameter) methodParameters.get(i);
			
			// math expected argument with method parameter type 
			ArgumentListItemNode arg = (ArgumentListItemNode) m.getCall().getArgumentListNode().getChildren().get(i);
			arg.setExpectedType(param.getType());

			TypedNode value = (TypedNode)arg.getChildren().get(0);

			// check assignability
			if (!lenseTypeSystem.isAssignableTo(value.getTypeVariable(), param.getType())) {
				throw new CompilationError(node, "Cannot assign " + value.getTypeVariable().getTypeDefinition().getName() + " to " + param.getType().getTypeDefinition().getName());
			}

			if (param.isMethodTypeBound()) {
				addFreeTypes(methodParameters, freeArguments, param.getType(), arg);
			}

		}

		Map<String, TypeVariable> boundedTypes = new HashMap<>();

		for (Map.Entry<String, List<ArgumentListItemNode>> entry : freeArguments.entrySet()) {

			if (entry.getValue().size() > 1) {
				TypeVariable previous = ((TypedNode)entry.getValue().get(0).getFirstChild()).getTypeVariable();

				for (int i = 1; i < entry.getValue().size(); i++) {
					TypeVariable current = ((TypedNode)entry.getValue().get(i).getFirstChild()).getTypeVariable();

					if (!lenseTypeSystem.isPromotableTo(current, previous)) {

						if (lenseTypeSystem.isPromotableTo(previous, current)) {
							previous = current;
						} else {
							throw new CompilationError(node, "Types " + previous.getTypeDefinition().getName() + " and " + current.getTypeDefinition().getName() + " cannot be bound to free type " + entry.getKey());
						}

					}
				}

				boundedTypes.put(entry.getKey(), previous);

			} else {
				TypeVariable previous = ((TypedNode)entry.getValue().get(0).getFirstChild()).getTypeVariable();
				boundedTypes.put(entry.getKey(), previous);
			}
		}

		m.setBoundedTypes(boundedTypes);
	}

	private void addFreeTypes(
			List<CallableMemberMember<Method>> methodParameters,
			Map<String, List<ArgumentListItemNode>> freeArguments, 
			TypeVariable paramType,
			ArgumentListItemNode arg
			) {
		
		if (paramType.getSymbol().isPresent()) {
			String symbol = paramType.getSymbol().get();
			List<ArgumentListItemNode> freeType = freeArguments.get(symbol);

			if (freeType == null) {
				freeType= new ArrayList<>(methodParameters.size());
				freeArguments.put(symbol, freeType);
			}

			freeType.add(arg);
		} else if(!paramType.getGenericParameters().isEmpty()){
			for(TypeVariable g : paramType.getGenericParameters()) {
				addFreeTypes(methodParameters, freeArguments, g, arg);
			}
		}
	}

	private ExpressionNode ensureExpression(AstNode node) {
		if (node instanceof IdentifierNode) {
			throw new RuntimeException(node + " is not an expression");
		} else if (node instanceof ExpressionNode) {
			return (ExpressionNode) node;
		} else {
			throw new RuntimeException(node + " is not an expression");
		}
	}

	private Optional<TypedNode> typeForName(AstNode access, String name) {

		VariableInfo variable = this.getSemanticContext().currentScope().searchVariable(name);

		if (variable != null) {
			return Optional.of(new VariableReadNode(name, variable));
		} else {

			// can be a field

			Optional<Field> field = currentType.getFieldByName(name);

			if (field.isPresent()) {
				return Optional.of(new FieldOrPropertyAccessNode(field.get()));
			} else {

				// can be a property
				Optional<Property> property = currentType.getPropertyByName(name);

				if (property.isPresent()) {
					return Optional.of(new FieldOrPropertyAccessNode(property.get()));
				} else {
					throw new CompilationError(access, "Identifier " + name + " was not defined");
				}

			}
		}
	}

	private LenseAstNode promoteArithmeticOperatorToMethodCall(ExpressionNode parent, ExpressionNode leftExpression,
			ExpressionNode rightExpression, ArithmeticOperation operation) {

		TypeVariable left = ensureNotFundamental(leftExpression.getTypeVariable());
		TypeVariable right = ensureNotFundamental(rightExpression.getTypeVariable());

		if (left.equals(right) && left.getTypeDefinition().equals(LenseTypeSystem.String())) {

			if (operation != ArithmeticOperation.Concatenation) {
				throw new CompilationError(parent,
						"Operation " + operation.equivalentMethod() + " is not defined for type String");
			}

			parent.setTypeVariable(left);

			StringConcatenationNode c;
			if (leftExpression instanceof StringConcatenationNode) {
				c = (StringConcatenationNode) leftExpression;
				c.add(rightExpression);
			} else {
				c = new StringConcatenationNode();
				c.add(leftExpression);
				c.add(rightExpression);

			}
			c.setTypeVariable(left);
			parent.getParent().replace(parent, c);

			return c;
		} else {
			// validate division by zero

			// find instance operator method
			TypeDefinition type = ensureNotFundamental(left.getTypeDefinition());

			if (type.equals(LenseTypeSystem.String())) {

				Optional<Method> method = type.getMethodBySignature(new MethodSignature("asString"));
				
				MethodInvocationNode convert = new MethodInvocationNode(method.get(), rightExpression);
				convert.setTypeVariable(left);

				StringConcatenationNode concat = new StringConcatenationNode();
				concat.add(leftExpression);
				concat.add(convert);
				concat.setTypeVariable(left);

				parent.getParent().replace(parent, concat);
			} else {
				MethodSignature signature = new MethodSignature(operation.equivalentMethod(),
						new MethodParameter(right, "text"));

				Optional<Method> method = type.getMethodBySignature(signature);

				if (!method.isPresent()) {

					method = type.getMethodByPromotableSignature(signature);

					if (!method.isPresent()) {
						// search static operator
						throw new CompilationError(parent,
								"Method " + operation.equivalentMethod() + "(" + right + ") is not defined in " + left);
					} else {
						// Promote
						promote(parent, rightExpression, left, right);
					}
				}

				ArgumentListItemNode arg = new ArgumentListItemNode(0, rightExpression);
				arg.setExpectedType(rightExpression.getTypeVariable());

				MethodInvocationNode invokeOp = new MethodInvocationNode(method.get(),leftExpression,arg);

				List<CallableMemberMember<Method>> methodParameters = method.get().getParameters();
				if (methodParameters.size() != invokeOp.getCall().getArgumentListNode().getChildren().size()) {
					throw new CompilationError(parent, "Argument count does not match parameters count");
				}

				for (int i = 0; i < methodParameters.size(); i++) {
					MethodParameter param = (MethodParameter) methodParameters.get(i);
					ArgumentListItemNode a = (ArgumentListItemNode) invokeOp.getCall().getArgumentListNode()
							.getChildren().get(i);
					a.setExpectedType(param.getType());
				}

				parent.getParent().replace(parent, invokeOp);

				TypeVariable t = method.get().getReturningType();
				if (t == null) {
					throw new IllegalStateException("Type cannot be null");
				}
				invokeOp.setTypeVariable(t);

				return invokeOp;
			}
		}
		return null;
	}

	private void promote(LenseAstNode parent, ExpressionNode rightExpression, TypeVariable left, TypeVariable right) {

		if (lenseTypeSystem.isAssignableTo(left, right)) {
			return;
		}

		Optional<Constructor> op = left.getTypeDefinition().getConstructorByParameters(Visibility.Public,
				new ConstructorParameter(right));

		if (!op.isPresent()) {
			throw new CompilationError(parent, "Implicit constructor not found to promote " + right + " to " + left);
		}
		if (rightExpression instanceof NumericValue) {
			NumericValue n = (NumericValue) rightExpression;

			n.setTypeVariable(left);
		} else {
			NewInstanceCreationNode cn = NewInstanceCreationNode.of(left, op.get(), rightExpression);
			cn.getCreationParameters().getTypeParametersListNode()
			.add(new GenericTypeParameterNode(new TypeNode(left)));

			parent.replace(rightExpression, cn);
		}

	}

	private TypeDefinition ensureNotFundamental(TypeDefinition type) {
		if (type instanceof LoadedLenseTypeDefinition) {
			return type;
		} else if (type instanceof FundamentalLenseTypeDefinition) {
			return getSemanticContext().resolveTypeForName(type.getName(), type.getGenericParameters().size())
					.orElseThrow(() -> new RuntimeException(type.getName() + " has not found")).getTypeDefinition();
		}
		return type;

	}

	private TypeVariable ensureNotFundamental(TypeVariable type) {

		type.ensureNotFundamental(t -> getSemanticContext()
				.resolveTypeForName(t.getName(), t.getGenericParameters().size()).get().getTypeDefinition());

		return type;

	}

	private void resolveFieldPropertyOrVariableName(AstNode node, FieldOrPropertyAccessNode m, TypeVariable currentType,
			TypeVariable fieldOwnerType, String name) {

		TypeDefinition def = currentType.getTypeDefinition();
		Optional<Field> field = def.getFieldByName(name);

		if (!field.isPresent()) {

			// try variable
			VariableInfo variable = this.getSemanticContext().currentScope().searchVariable(name);

			if (variable == null) {

				Optional<Property> property = def.getPropertyByName(name);

				if (!property.isPresent()) {

					// check possible object reference

					Optional<TypeVariable> object = this.getSemanticContext().resolveTypeForName(name, 0);

					if (!object.isPresent()) {
						throw new CompilationError(node, "Field " + name + " is not defined in " + fieldOwnerType);
					}

					m.setTypeVariable(object.get());

					m.getParent().replace(m, new ObjectReadNode(object.get(), name));

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
		TypeVariable type = methodOwnerType.getGenericParameters().get(1).getUpperBound();
		while (!lenseTypeSystem.isAssignableTo(type.getTypeDefinition(), LenseTypeSystem.Nothing())) {
			count++;

			type = type.getGenericParameters().get(1).getUpperBound();
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
				return new MethodParameter(var.getTypeVariable(), var.getName());
			} else if (v instanceof MethodInvocationNode) {
				MethodInvocationNode var = (MethodInvocationNode) v;
				return new MethodParameter(var.getTypeVariable(), "methodParam");
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
					return new MethodParameter(var.getTypeVariable(), "type");
				}

			} else if (v instanceof QualifiedNameNode) {
				QualifiedNameNode qn = (QualifiedNameNode) v;

				Optional<TypeVariable> maybeType = this.getSemanticContext().resolveTypeForName(qn.getName(), 0);

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
				return new MethodParameter(maybeType.get(), "");
			} else if (v instanceof IdentifierNode) {
				VariableInfo var = this.getSemanticContext().currentScope()
						.searchVariable(((IdentifierNode) v).getName());

				if (var == null) {
					throw new CompilationError(v, ((IdentifierNode) v).getName() + " is not a field or variable");
				}

				return new MethodParameter(var.getTypeVariable(), var.getName());
			} else {
				throw new RuntimeException();
			}
		}).collect(Collectors.toList()).toArray(new MethodParameter[argumentListNode.getChildren().size()]);
	}

}