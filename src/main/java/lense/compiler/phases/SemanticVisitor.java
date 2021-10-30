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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.parser.IdentifierNode;
import compiler.parser.NameIdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.JuxpositionNode;
import lense.compiler.TypeAlreadyDefinedException;
import lense.compiler.TypeMembersNotLoadedError;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ArgumentListHolder;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.BreakNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldAccessKind;
import lense.compiler.ast.FieldOrPropertyAccessNode.FieldKind;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.GivenGenericConstraint;
import lense.compiler.ast.GivenGenericConstraintList;
import lense.compiler.ast.IndexedPropertyReadNode;
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
import lense.compiler.ast.ReceiveReifiedTypesNodes;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeOfInvocation;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.UnitaryOperation;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.crosscompile.PrimitiveBooleanValue;
import lense.compiler.crosscompile.VariableRange;
import lense.compiler.type.CallableMember;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.Field;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Match;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.MethodSignature;
import lense.compiler.type.Property;
import lense.compiler.type.TypeAssistant;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.UnionType;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.RecursiveTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.type.variable.UpdatableTypeVariable;
import lense.compiler.typesystem.FundamentalLenseTypeDefinition;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public final class SemanticVisitor extends AbstractScopedVisitor {

	private Map<String, List<Method>> expectedMethods = new HashMap<String, List<Method>>();

	private LenseTypeDefinition ANY;
	private LenseTypeDefinition VOID;

	protected LenseTypeDefinition currentType;

	private final Map<TypeVariable, List<TypeDefinition>> enhancements;

	private final TypeAssistant typeAssistant;

	private CompilerListener listener;

	private Set<TypeMember> declaredMembers = new HashSet<>();

	public SemanticVisitor(
		SemanticContext semanticContext, 
		CompilerListener listener,
		Map<TypeVariable, List<TypeDefinition>> enhancements
	) {
		super(semanticContext);

		this.listener = listener;

		typeAssistant =  new LenseTypeAssistant(semanticContext);

		ANY = (LenseTypeDefinition) semanticContext.resolveTypeForName("lense.core.lang.Any", 0).get();
		VOID = (LenseTypeDefinition) semanticContext.resolveTypeForName("lense.core.lang.Void", 0).get();

		this.enhancements = Collections.unmodifiableMap(enhancements);

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
							if (!m.isNative() && !m.isPropertyBridge() && m.isAbstract() && !m.isDefault()) {
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

			currentType.getConstructors().filter(m -> m.getName() == null).forEach(c -> {

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
								Collection<Method> implemented = typeAssistant.getMethodsByName(currentType, m.getName());
								if (!implemented.stream().anyMatch(i -> typeAssistant.isMethodImplementedBy(m, i))) {
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
		 if (node instanceof ContinueNode) {
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
				constructorDeclarationNode.setVisibility(currentType.getVisibility()); // set the class visibility level
			}

			constructorDeclarationNode
					.setReturnType(new TypeNode(this.getSemanticContext().currentScope().getCurrentType()));

			// define variable in the method scope. the current scope is block
			this.getSemanticContext().currentScope().defineVariable("@returnOfMethod",
					this.getSemanticContext().currentScope().getCurrentType(), node);

			var cparams = new LinkedList<ConstructorParameter>();

			for (var p : constructorDeclarationNode.getParameters().getChildren(FormalParameterNode.class)) {
				if (!(p instanceof ReceiveReifiedTypesNodes)) {
					cparams.add(new ConstructorParameter(p.getTypeVariable(), p.getName()));
				}
			}

			if (constructorDeclarationNode.isImplicit() && cparams.size() != 1) {
				throw new CompilationError(node,
						"An implicit constructor must have exactly one parameter. Found " + cparams.size());
			}

			var constructor = new Constructor(constructorDeclarationNode.getName(), cparams,
					constructorDeclarationNode.isImplicit(), constructorDeclarationNode.getVisibility());

			currentType.addConstructor(constructor);

			constructorDeclarationNode.setConstructor(constructor);

			if (!this.declaredMembers.add(constructor)) {
				if (constructor.getName() == null) {
					throw new CompilationError(node, "The constructor " + constructor.getParameters()
							+ " was already declared in type " + this.currentType.getName());
				} else {
					throw new CompilationError(node,
							"The constructor '" + constructor.getName() + constructor.getParameters()
									+ "' was already declared in type " + this.currentType.getName());
				}

			}
			
			var extention = constructorDeclarationNode.getExtention();
			var superType = this.getCurrentType().get().getSuperDefinition();
			if (!typeAssistant.isAny(superType)) {
				var count  = superType.getAllMembers().stream()
						.filter(c -> c.isConstructor())
						.collect(Collectors.counting());
					
				
				if (extention == null) {
					if (count > 0) {
						var possibleNoParamsConstructor = superType.getAllMembers().stream()
						.filter(c -> c.isConstructor())
						.map(c -> (Constructor)c)
						.filter(c -> c.getParameters().size() == 0)
						.findAny();
						
						if (possibleNoParamsConstructor.isEmpty()) {
							throw new CompilationError(node, "The super constructor with no parameters is not declared in type " + superType.getName());
						}
						
						checkAccess(superType, possibleNoParamsConstructor.get());
					} 
				} else {
					// match parameters
					if (count > 0) {
						var possibleConstructors = superType.getAllMembers().stream()
								.filter(c -> c.isConstructor())
								.map(c -> (Constructor)c)
								.filter(c -> c.getParameters().size() == extention.getArguments().getChildren().size())
								.collect(Collectors.toList());
						
						if (possibleConstructors.isEmpty()) {
							throw new CompilationError(node, "The super constructor " + extention.getArguments().getChildren() +" is not declared in type " + superType.getName());
						}
					} else if (!extention.getArguments().getChildren().isEmpty()){
						throw new CompilationError(node, "The super constructor " + extention.getArguments().getChildren() +" is not declared in type " + superType.getName());
					}
				}
				
			}
			

		} else if (node instanceof AssignmentNode) {
			AssignmentNode a = (AssignmentNode) node;

			if (a.getLeft() instanceof FieldOrPropertyAccessNode left) {
				left.setAccessKind(FieldAccessKind.WRITE);
			}

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

			List<MethodParameter> params = new ArrayList<>(m.getParameters().getChildren().size());

			for (FormalParameterNode p : m.getParameters().getChildren(FormalParameterNode.class)) {
				params.add(new MethodParameter(p.getTypeVariable(), p.getName()));
			}

			var method = new Method(false, m.getVisibility(), m.getName(), new MethodReturn(m.getReturnType().getTypeVariable()), params);

			method.setDeclaringType(this.currentType);

			m.setMethod(method);

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

					if (m.getProperty("removed", Boolean.class).orElse(false)) {
						// already tested has matched in previous visit
						removeExpected(m.getMethod());
					} else {
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

										boolean matches = typeAssistant
												.isAssignableTo(thisParameter.getTypeVariable(),
														superParameter.getType().getUpperBound())
												.and(typeAssistant.isAssignableTo(
														superParameter.getType().getLowerBound(),
														thisParameter.getTypeVariable()))
												.matches();

										if (!matches) {
											analiseInheritance = false;
											break;
										}
									}
								}

								if (analiseInheritance) {

									if (!superMethod.isAbstract()) {
										if (superMethod.getDeclaringType().getKind().isTypeClass()) {
											if ( !m.isSatisfy()) {
												throw new CompilationError(node,
														"The method " + m.getName() + " in type " + this.currentType.getName()
																+ " must declare satisfy of a type class method in "
																+ superMethod.getDeclaringType().getName());
											}
										} else {
											if ( !m.isOverride()) {
												throw new CompilationError(node,
														"The method " + m.getName() + " in type " + this.currentType.getName()
																+ " must declare override of a supertype method in "
																+ superMethod.getDeclaringType().getName());
											}
											
											if (!superMethod.isDefault()) {
												throw new CompilationError(node,
														"The method " + m.getName() + " in type " + this.currentType.getName()
																+ " cannot override a non default supertype method in "
																+ superMethod.getDeclaringType().getName());
											}
										}
										
									
									}

								
									
									if (superMethod.getVisibility().isMoreVisibleThan(m.getVisibility())) {
										throw new CompilationError(node,
												"The method " + m.getName() + " in type " + this.currentType.getName()
														+ " cannot declare the method in "
														+ superMethod.getDeclaringType().getName()
														+ " with less visibility");
									}

									m.setSuperMethod(superMethod);

									m.getMethod().setSuperMethod(superMethod);
									removeExpected(m.getMethod());

									m.setProperty("removed", true);

									Iterator<CallableMemberMember<Method>> itSuper = superMethod.getParameters()
											.iterator();
									Iterator<CallableMemberMember<Method>> itMy = m.getMethod().getParameters()
											.iterator();

									while (itSuper.hasNext()) {
										MethodParameter s = (MethodParameter) itSuper.next();
										MethodParameter b = (MethodParameter) itMy.next();

										if (s.getType().isCalculated()) {
											b.setVariance(Variance.ContraVariant);
										}
									}

								}

							} // else, not the same number of parameters: is an overload.
						}
					}

				}
			}

			// verify duplication

			if (!this.declaredMembers.add(m.getMethod())) {
				throw new CompilationError(node,
						"The method '" + m.getName() + "' was already declared in type " + this.currentType.getName());
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

			Optional<TypeVariable> maybeMyType = this.getSemanticContext().resolveTypeForName(t.getFullname(),
					genericParametersCount);

			LenseTypeDefinition myType = null;
			if (maybeMyType.isPresent()) {
				myType = (LenseTypeDefinition) maybeMyType.get();
			} 
			
			this.currentType = myType;
			
			// givens 
			var givens = t.getGivens();
			
			var givenMap = new HashMap<String,GivenGenericConstraint >();
			
			if (givens != null) {
				
				for(var given : givens.getChildren(GivenGenericConstraint.class)) {
					givenMap.put(given.getName(), given);
				}
			}
			
			// generics
			if (myType == null || myType.getGenericParameters().size() != genericParametersCount) {
				List<TypeVariable> genericVariables = new ArrayList<>(genericParametersCount);

				Map<String, UpdatableTypeVariable> updateBonds = new HashMap<>();

				if (genericParametersCount > 0) {

					for (AstNode a : t.getGenerics().getChildren()) {
						GenericTypeParameterNode g = (GenericTypeParameterNode) a;

						TypeNode tn = g.getTypeNode();

						var r =  RangeTypeVariable.allRange(tn.getName(), g.getVariance());
						
						var u = new UpdatableTypeVariable(r);
						
						genericVariables.add(u);
						updateBonds.put(tn.getName(), u);
						
						this.getSemanticContext().currentScope().defineTypeVariable(tn.getName(), r, node);
					
					}

				}

				myType = new LenseTypeDefinition(t.getFullname(), t.getKind(), ANY, genericVariables);
				myType = (LenseTypeDefinition) this.getSemanticContext().registerType(myType, genericParametersCount);
				
				if (!updateBonds.isEmpty()) {
					for (var entry : givenMap.entrySet()) {
						var givenType = this.getSemanticContext().typeForName( entry.getValue().getTypeNode()).getTypeDefinition();
						
						if (givenType.equals(myType)) {
							// T is recursive
							UpdatableTypeVariable u = updateBonds.get(entry.getKey());
							
							u.update(new RecursiveTypeVariable(myType));	
						} else {
							UpdatableTypeVariable u = updateBonds.get(entry.getKey());
							
							RangeTypeVariable r = (RangeTypeVariable)u.original();
							
							r.setUpperBound(givenType);	
						}
						
					}
				}

			}

			this.currentType = myType;

			myType.setKind(t.getKind());

			if (myType.getKind().isInterface() || myType.getKind().isTypeClass()) {
				myType.setAbstract(true);
			} else {
				myType.setAbstract(t.isAbstract());
			}

			Visibility visibility = t.getVisibility();
			if (visibility == Visibility.Undefined) {
				if (myType.getKind().isInterface()) {
					visibility = Visibility.Public;	
				}
				visibility = Visibility.Protected;
			} else if ( visibility == Visibility.Private) {
				throw new CompilationError(node, "Types cannot be private");
			}
			myType.setVisibility(visibility);
	
			myType.setNative(t.isNative());
			myType.setExplicitlyImmutable(t.isImmutable());
			// TODO annotations

			if (t.getKind().isValue() && !t.isImmutable()) {
				this.listener.warn(new CompilerMessage(
						"Value classes must be immutable. You must remove the mutable modifier for "
								+ t.getFullname()));
			}

			if (t.getKind().isValue()) {
				// if is value, is final
				t.setFinal(true);
				myType.setFinal(true);
			}

			TypeNode superTypeNode = t.getSuperType();

			if (t.getKind().isEnhancement()) {
				if (superTypeNode == null) {
					throw new CompilationError(node, t.getFullname() + " enhancement must define a type for extention");
				}
			}
			
			
			for(var given :givenMap.values()) {
				var givenType = this.getSemanticContext().typeForName( given.getTypeNode()).getTypeDefinition();
				
				var rangetype = RangeTypeVariable.upTo(given.getName(), Variance.Invariant,givenType);
				this.getSemanticContext().currentScope().defineTypeVariable(given.getName(),rangetype, node);
			}
			
			
			TypeDefinition superType = ANY;
			if (superTypeNode != null) {

				if (superTypeNode.getName().equals(myType.getName())) {
					throw new CompilationError(t, "Type cannot inherit from it self");
				}

				superType = this.getSemanticContext().typeForName(superTypeNode).getTypeDefinition();

				if (t.getKind().isValue()) {
					throw new CompilationError(t,
							"Value classes cannot inherit from other types. They can only implement interfaces");
				}
				
				checkAccess(superType);

				if (superType.isGeneric()) {

					if (t.getKind().isEnhancement()) {

						List<TypeVariable> params = new ArrayList<>(superTypeNode.getChildren().size());
						for (AstNode n : superTypeNode.getChildren()) {
							if (n instanceof GenericTypeParameterNode) {
								GenericTypeParameterNode g = (GenericTypeParameterNode) n;

								params.add(this.getSemanticContext().typeForName(g.getTypeNode()));

							} else {
								throw new UnsupportedOperationException();
							}
						}

						superType = typeAssistant.specify(superType, params);

					} else {
						for (AstNode n : superTypeNode.getChildren()) {
							if (n instanceof GenericTypeParameterNode) {
								// throw new UnsupportedOperationException();
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

									interfaceType = typeAssistant.specify(rawInterfaceType, parameters);

								}

								tn.setTypeVariable(interfaceType);
								myType.addInterface(interfaceType);
							}

						}
					}

				}

				if (!t.getKind().isEnhancement() && superType.getKind() == LenseUnitKind.Interface
						&& !typeAssistant.isAny(superType)) {
					throw new CompilationError(node, t.getFullname() + " cannot extend interface " + superType.getName()
							+ ". Did you meant to use 'implements' instead of 'extends' ?.");
				}

				superTypeNode.setTypeVariable(superType);

			}

			if (superType.equals(myType)) {
				if (!myType.equals(ANY)) {
					throw new CompilationError(node, t.getFullname() + " cannot extend it self");
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
			if (t.isAlgebric() && !t.isNative()) {
				myType.setAlgebric(true);

				List<TypeDefinition> chidlValues = new ArrayList<>(t.getAlgebricChildren().getChildren().size());
				List<TypeDefinition> chidlTypes = new ArrayList<>(t.getAlgebricChildren().getChildren().size());

				for (AstNode n : t.getAlgebricChildren().getChildren()) {
					ChildTypeNode ctn = (ChildTypeNode) n;

					var resolverType = this.getSemanticContext()
							.resolveTypeForName(ctn.getType().getName(), ctn.getType().getTypeParametersCount());
					
					if (!resolverType.isPresent()) {
						
						final var currentType = myType;
						resolverType = t.getParent().getChildren().stream()
							.filter(it -> it instanceof ClassTypeNode)
							.map(it -> (ClassTypeNode)it)
							.filter(it -> it.getSimpleName().equals(ctn.getType().getName()))
							.findAny()
							.map(it -> new LenseTypeDefinition(it.getFullname(), it.getKind(), currentType));
						
					}
					
					TypeDefinition childType = resolverType.get().getTypeDefinition();

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

			t.setAbstract(myType.isAbstract());
			t.setTypeDefinition(myType);

			if (t.getKind().isEnhancement()) {

				this.getSemanticContext().currentScope().defineVariable("this", superType, node).setInitialized(true);

			} else {
				this.getSemanticContext().currentScope().defineVariable("this", myType, node).setInitialized(true);

				this.getSemanticContext().currentScope().defineVariable("super", superType, node).setInitialized(true);

			}

			if (t.getInterfaces() != null) {
				for (AstNode n : t.getInterfaces().getChildren()) {

					TypeDefinition interfaceType = specifySuperType(myType, myType.getGenericParameters(),
							(TypeNode) n);

					if (interfaceType.getName().equals(myType.getName())) {
						throw new CompilationError(t, "Type cannot implement it self");
					}

					checkAccess(interfaceType);
					
					myType.addInterface(interfaceType);

					for (TypeMember m : interfaceType.getAllMembers()) {
						if (m.isMethod() && !m.isProperty()) {
							addExpected(m.getName(), ((Method) m));

						}
					}

				}
			}
			
			if (t.getSatisfiedTypeClasses() != null) {
				for (AstNode n : t.getSatisfiedTypeClasses().getChildren()) {

					TypeDefinition typeClassType = specifySuperType(myType, myType.getGenericParameters(),
							(TypeNode) n);

					if (typeClassType.getName().equals(myType.getName())) {
						throw new CompilationError(t, "Type cannot implement it self");
					}

					checkAccess(typeClassType);
					
					myType.addTypeClass(typeClassType);

					for (TypeMember m : typeClassType.getAllMembers()) {
						if (m.isMethod() && !m.isProperty()) {
							addExpected(m.getName(), ((Method) m));

						}
					}

				}
			}

			TreeTransverser.transverse(t,
					new StructureVisitor(this.listener, myType, this.getSemanticContext(), this.enhancements, true));


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

			TreeTransverser.transverse(n.getContainer(),
					new SemanticVisitor(this.getSemanticContext(), this.listener, this.enhancements));

			TypeVariable containerTypeVariable = n.getContainer().getTypeVariable();

			TypeVariable typeVariable = containerTypeVariable.getGenericParameters().get(0);

			n.getVariableDeclarationNode().setTypeNode(new TypeNode(typeVariable));

			VariableInfo iterationVariable = this.getSemanticContext().currentScope()
					.defineVariable(n.getVariableDeclarationNode().getName(), typeVariable, n);

			iterationVariable.setInitialized(true);

			if (typeAssistant.isAssignableTo(n.getContainer().getTypeVariable(), LenseTypeSystem.Progression())
					.matches()) {

				if (typeAssistant.isAssignableTo(iterationVariable.getTypeVariable(), LenseTypeSystem.Number())
						.matches()) { // TODO Orderable with successor

					VariableRange r = VariableRange.extractFrom(n.getContainer());

					r.getMin().ifPresent(v -> iterationVariable.setMininumValue(v));
					r.getMax().ifPresent(v -> iterationVariable.setMaximumValue(v));
					iterationVariable.setIncludeMaximum(r.isIncludeMax());
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
			return true;
		} else if (parent instanceof PropertyDeclarationNode || parent instanceof MethodDeclarationNode) {
			return false;
		} else {
			return isUsedInLoop(parent);
		}
	}

	private void removeExpected(Method method) {
		expectedMethods.remove(method.getName());
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

						TypeVariable p = this.getSemanticContext().ensureNotFundamental(current.getParameters().get(i).getType());
						TypeVariable n = this.getSemanticContext().ensureNotFundamental(method.getParameters().get(i).getType());

						if (typeAssistant.isAssignableTo(p, n).matches()) {
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

	private TypeDefinition specifySuperType(LenseTypeDefinition declaringType, List<TypeVariable> implementationGenericTypes, TypeNode interfaceNode) {

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
			TypeNode genericTypeParameter = g.getTypeNode();
			
			if (genericTypeParameter.getName().equals(declaringType.getName())) {
				genericTypeParameter.setTypeVariable(declaringType);
				parameters[index] = genericTypeParameter.getTypeVariable();
			} else if (!implementationGenericTypes.isEmpty() && (genericTypeParameter.getTypeVariable() == null
					|| genericTypeParameter.getTypeVariable().getTypeDefinition().equals(ANY))) {
				for (int i = 0; i < implementationGenericTypes.size(); i++) {
					TypeVariable v = implementationGenericTypes.get(i);
					if (v.getSymbol().get().equals(genericTypeParameter.getName())) {

						parameters[index] = new DeclaringTypeBoundedTypeVariable(declaringType, i,
								genericTypeParameter.getName(), g.getVariance());

					}
				}
				
				if (parameters[index] == null) {
					
					TypeDefinition type = this.getSemanticContext().typeForName(genericTypeParameter).getTypeDefinition();
					genericTypeParameter.setTypeVariable(type);
					parameters[index] = type;
				
				}

			} else {
				if (genericTypeParameter.getTypeParametersCount() > 0) {
					// Recursive call
					parameters[index] = specifySuperType(declaringType, implementationGenericTypes,
							genericTypeParameter);

				} else if (this.currentType.getSimpleName().equals(genericTypeParameter.getName())) {
					genericTypeParameter.setTypeVariable(currentType);
					parameters[index] = currentType;
				} else {
					TypeDefinition type = this.getSemanticContext().typeForName(genericTypeParameter).getTypeDefinition();
					genericTypeParameter.setTypeVariable(type);
					parameters[index] = type;
				}
			}

			index++;
		}

		for (var item : parameters) {
			if (item == null) {
				throw new RuntimeException();
			}
		}
		TypeDefinition interfaceType = typeAssistant.specify(rawInterfaceType, parameters);
		interfaceNode.setTypeVariable(interfaceType);

		return interfaceType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doVisitAfterChildren(AstNode node) {
		if (node instanceof InstanceOfNode) {
			InstanceOfNode n = (InstanceOfNode) node;

			if (!typeAssistant.isAssignableTo(n.getExpression().getTypeVariable(), n.getTypeNode().getTypeVariable())
					.matches()
					&& !typeAssistant
							.isAssignableTo(n.getTypeNode().getTypeVariable(), n.getExpression().getTypeVariable())
							.matches()) {

				// is mandatory false
				n.setMandatoryEvaluation(false);

			}

		} else if (node instanceof JuxpositionNode) {
			JuxpositionNode jn = (JuxpositionNode) node;

			ExpressionNode left = ensureExpression(jn.getChildren().get(0));
			ExpressionNode right = ensureExpression(jn.getChildren().get(1));

			TypeVariable posBindable = this.getSemanticContext().resolveTypeForName("lense.core.lang.PosBindable", 2)
					.get();
			TypeVariable preBindable = this.getSemanticContext().resolveTypeForName("lense.core.lang.PreBindable", 2)
					.get();

			// 1. check if left is PosBindable left.posBindWith(right)

			if (typeAssistant.isAssignableTo(left.getTypeVariable(), posBindable).matches()) {

				Optional<Method> method = typeAssistant.getMethodByPromotableSignature(left.getTypeVariable().getTypeDefinition(),
						new MethodSignature("posBindWith", new MethodParameter(right.getTypeVariable())));

				if (method.isPresent()) {
					ArgumentListItemNode arg = new ArgumentListItemNode(0, right);
					arg.setExpectedType(method.get().getParameters().get(0).getType());
					MethodInvocationNode m = new MethodInvocationNode(left, "posBindWith", arg);

					node.getParent().replace(node, m);

					m.setTypeVariable(method.get().getReturningType());
				} else {
					throw new CompilationError(left,
							"Expression is not bindable to " + right.getTypeVariable().toString());
				}

			} else {
				// 2. check if left as an extension method equivalent to left.posBindWith(right)
				// TODO

				// 3. check if right is PresBindable right.preBindWith(left)
				if (typeAssistant.isAssignableTo(right.getTypeVariable(), preBindable).matches()) {

					Optional<Method> method = typeAssistant.getMethodByPromotableSignature(right.getTypeVariable().getTypeDefinition(),
									new MethodSignature("preBindWith", new MethodParameter(left.getTypeVariable())));

					if (method.isPresent()) {
						ArgumentListItemNode arg = new ArgumentListItemNode(0, left);
						arg.setExpectedType(method.get().getParameters().get(0).getType());
						MethodInvocationNode m = new MethodInvocationNode(right, "preBindWith", arg);

						node.getParent().replace(node, m);

						m.setTypeVariable(method.get().getReturningType());
					} else {
						throw new CompilationError(node,
								"Expression is not bindable to " + left.getTypeVariable().toString());
					}

				} else {

					// 4. check if right as an exention method equivalent to right.preBindWith(left)
					// TODO

					// else fail
					throw new CompilationError(node, "Expressions are not bindable");
				}
			}

		} else if (node instanceof SwitchNode) {
			SwitchNode switchNode = (SwitchNode) node;

			TypeDefinition type = this.getSemanticContext().ensureNotFundamental(switchNode.getCandidate().getTypeVariable().getTypeDefinition());
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

			if (node instanceof ComparisonNode) {
				ComparisonNode n = (ComparisonNode) node;

				TypeDefinition comparable = LenseTypeSystem.Comparable();
				TypeVariable leftSide = this.getSemanticContext().ensureNotFundamental(n.getLeft().getTypeVariable());

				if (n.getOperation().dependsOnComparable()
						&& !typeAssistant.isAssignableTo(leftSide, comparable).matches()) {
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
//					TODO move to tidy up
					// if no constructor exists, add a default one

					var constructor = new Constructor("constructor", Collections.emptyList(), false, Visibility.Public);

					currentType.addConstructor(constructor);

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

				TypeDefinition maxType = ((TypedNode) literal.getArguments().getFirstArgument().getFirstChild())
						.getTypeVariable().getTypeDefinition();

				maxType = this.getSemanticContext()
						.resolveTypeForName(maxType.getName(), maxType.getGenericParameters().size()).get()
						.getTypeDefinition();

				boolean isMaybe = typeAssistant.isMaybe(maxType);

				for (int i = 1; i < literal.getArguments().getChildren().size(); i++) {
					AstNode n = literal.getArguments().getChildren().get(i).getFirstChild();
					TypedNode t = (TypedNode) n;
					TypeDefinition nextType = t.getTypeVariable().getTypeDefinition();
					nextType = this.getSemanticContext()
							.resolveTypeForName(nextType.getName(), nextType.getGenericParameters().size()).get()
							.getTypeDefinition();

					if (!nextType.equals(maxType)) {
						if (typeAssistant.isPromotableTo(maxType, nextType)) {
							maxType = nextType;
						} else if (!typeAssistant.isPromotableTo(nextType, maxType)) {

							isMaybe = isMaybe || this.typeAssistant.isMaybe(nextType);

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
					maxType = typeAssistant.specify(maybeType, maxType);

					TypeDefinition someType = this.getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1)
							.get().getTypeDefinition();

					TypeVariable innerTypeVar = innerType;

					ListIterator<AstNode> lstIterator = literal.getArguments().listIterator();

					while (lstIterator.hasNext()) {
						AstNode n = lstIterator.next().getFirstChild();
						TypeDefinition type = ((TypedNode) n).getTypeVariable().getTypeDefinition();

						if (typeAssistant.isMaybe(type)) {
							continue;
						}
						if (!typeAssistant.isAssignableTo(type, innerType).matches()) {
							if (typeAssistant.isPromotableTo(type, innerType)) {

								Constructor op = optionalConstructor(innerType, new ConstructorParameter(type));

								NewInstanceCreationNode cn = NewInstanceCreationNode.of(op, n);
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
						if (!typeAssistant.isMaybe(type)) {

							Constructor op = optionalConstructor(someType, new ConstructorParameter(type));

							TypeVariable someTypeSpec = typeAssistant.specify(someType, type);

							NewInstanceCreationNode cn = NewInstanceCreationNode.of(someTypeSpec, op, n);
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
						if (!typeAssistant.isAssignableTo(type, maxType).matches()) {
							if (typeAssistant.isPromotableTo(type, maxType)) {

								Constructor op = optionalConstructor(maxType, new ConstructorParameter(type));

								NewInstanceCreationNode cn = NewInstanceCreationNode.of(op, n);
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

				TypeVariable seqType = typeAssistant.specify(sequenceType.get(), maxType);

				literal.setTypeVariable(seqType);

				TypeParametersListNode typeParametersListNode = literal.getCreationParameters()
						.getTypeParametersListNode();

				typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(seqType)));

			} else if (node instanceof LiteralAssociationInstanceCreation) {
				LiteralAssociationInstanceCreation literal = (LiteralAssociationInstanceCreation) node;

				TypeDefinition keypair = ((TypedNode) literal.getArguments().getFirstArgument().getFirstChild())
						.getTypeVariable().getTypeDefinition();
				TypeVariable keyType = keypair.getGenericParameters().get(0);
				TypeVariable valueType = keypair.getGenericParameters().get(1);

				literal.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(keyType)));
				literal.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(valueType)));

				for (ArgumentListItemNode a : literal.getArguments().getChildren(ArgumentListItemNode.class)) {
					NewInstanceCreationNode n = ((NewInstanceCreationNode) a.getFirstChild());

					TypeParametersListNode typeParametersListNode = n.getCreationParameters()
							.getTypeParametersListNode();

					typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(keyType)));
					typeParametersListNode.add(new GenericTypeParameterNode(new TypeNode(valueType)));

					a.setExpectedType(typeAssistant.specify(keypair, keyType, valueType));
				}

				literal.setTypeVariable(typeAssistant.specify(LenseTypeSystem.Association(), keyType, valueType));

			} else if (node instanceof LiteralTupleInstanceCreation) {

				LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);

				TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0).getFirstChild();
				TypedNode nextTuple;
				if (tuple.getChildren().get(1).getChildren().size() == 2) {
					nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1).getFirstChild();
				} else {
					nextTuple = new TypeNode(LenseTypeSystem.Nothing());
				}

				var tupleType = typeAssistant.specify(LenseTypeSystem.Tuple(),
						value.getTypeVariable(), nextTuple.getTypeVariable());

				tuple.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable())));
				tuple.getCreationParameters().getTypeParametersListNode()
						.add(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));

				((LiteralTupleInstanceCreation) node).setTypeVariable(tupleType);
			} else if (node instanceof RangeNode) {
				RangeNode r = (RangeNode) node;

				final ExpressionNode left = (ExpressionNode) r.getChildren().get(0);
				final ExpressionNode right = (ExpressionNode) r.getChildren().get(1);
				TypeVariable innerType = tryPromoteEnds(node, left, right)
						.orElseThrow(() -> new CompilationError(node, "Cannot create range from "
								+ left.getTypeVariable() + " to " + right.getTypeVariable()));

				r.setTypeVariable(typeAssistant.specify(LenseTypeSystem.Progression(), innerType));

				ArgumentListItemNode arg = new ArgumentListItemNode(0, r.getEnd());
				arg.setExpectedType(r.getEnd().getTypeVariable());

				String name = "upTo";
				if (!r.isIncludeEnd()) {
					name = "upToExclusive";
				}

				VariableRange limits = VariableRange.extractFrom(r);

				MethodInvocationNode create = new MethodInvocationNode(r.getStart(), name, arg);
				create.setTypeVariable(
						typeAssistant.specify(LenseTypeSystem.Progression(), r.getStart().getTypeVariable()));

				limits.getMin().ifPresent(m -> create.setProperty("minimum", m));
				limits.getMax().ifPresent(m -> create.setProperty("maximum", m));

				create.setProperty("includeMaximum", r.isIncludeEnd());
				create.setProperty("isRange", "true");

				r.getParent().replace(r, create);

			} else if (node instanceof LiteralIntervalNode) {
				LiteralIntervalNode r = (LiteralIntervalNode) node;

				final ExpressionNode left = (ExpressionNode) r.getChildren().get(0);
				final ExpressionNode right = (ExpressionNode) r.getChildren().get(1);

				TypeVariable innerType = tryPromoteEnds(node, left, right)
						.orElseThrow(() -> new CompilationError(node, "Cannot create interval from "
								+ left.getTypeVariable() + " to " + right.getTypeVariable()));

				TypeVariable type = this.getSemanticContext().resolveTypeForName("lense.core.math.Interval", 1).get();

				r.setTypeVariable(typeAssistant.specify(type, innerType));

			} else if (node instanceof LambdaExpressionNode) {
				LambdaExpressionNode n = (LambdaExpressionNode) node;

				List<TypeVariable> generics = new ArrayList<>();

				generics.add(n.getBody().getTypeVariable());

				// TODO infer types
				for (AstNode v : n.getParameters().getChildren()) {
					FormalParameterNode vr = (FormalParameterNode) v;
					generics.add(vr.getTypeVariable());
				}

				TypeDefinition funtionType = typeAssistant.specify(LenseTypeSystem.Function(generics.size()),
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
				Optional<Method> list = typeAssistant.getMethodsByName(type, methodName).stream()
						.filter(md -> md.getParameters().size() == 0).findAny();

				if (!list.isPresent()) {
					throw new CompilationError(node,
							"The method " + methodName + "() is undefined for TypeDefinition " + type);
				}

				checkAccess(list.get().getDeclaringType(),list.get());
				
//				TODO move to transformations. use an operator call node
				// replace by a method invocation
				MethodInvocationNode method = new MethodInvocationNode(list.get(),ensureExpression(node.getChildren().get(0)));
				
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

				final TypeDefinition type = this.getSemanticContext().ensureNotFundamental(((TypedNode) p.getChildren().get(0)).getTypeVariable().getTypeDefinition());

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

					Optional<Method> list = typeAssistant.getMethodsByName(type,methodName).stream()
							.filter(md -> md.getParameters().isEmpty())
							.findAny();

					if (!list.isPresent()) {
						throw new CompilationError(node,
								"The method " + methodName + "() is undefined for TypeDefinition " + type);
					}

					checkAccess(list.get().getDeclaringType(),list.get());
					
//					TODO move to transformations. use an operator call node
					// replace by a method invocation
					MethodInvocationNode method = new MethodInvocationNode(list.get(),
							ensureExpression(node.getChildren().get(0)));
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
					Optional<Method> foundMethod = typeAssistant.getMethodBySignature(type,MethodSignature.forName(methodName))
							.stream()
							.filter(md -> md.getParameters().isEmpty())
							.findAny();

					if (!foundMethod.isPresent()) {
						throw new CompilationError(node,
								"The method " + methodName + "() is undefined for TypeDefinition " + type);
					}

					checkAccess(foundMethod.get().getDeclaringType(), foundMethod.get());
					
//					TODO move to transformations. use an operator call node
					// replace by a method invocation
					MethodInvocationNode method = new MethodInvocationNode(foundMethod.get(),
							ensureExpression(node.getChildren().get(0)));

					method.setTypeVariable(foundMethod.get().getReturningType());

					node.getParent().replace(node, method);
				}

			} else if (node instanceof PrimitiveBooleanValue) {
				// no-op
			} else if (node instanceof LiteralExpressionNode) {
				LiteralExpressionNode n = (LiteralExpressionNode) node;

				this.getSemanticContext()
						.resolveTypeForName(n.getTypeVariable().getTypeDefinition().getName(), 0)
						.ifPresent( it -> n.setTypeVariable(it));

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
					if (typeAssistant.isBoolean(type)) {
						methodName = "negate";
					} else {
						throw new CompilationError(node,
								"Operator ! can only be applied to Boolean instances ( found " + type.getName() + ")");
					}
				} else {
					throw new CompilationError(node, "Unrecognized operator");
				}

				Optional<Method> list = typeAssistant.getMethodsByName(type,methodName).stream()
						.filter(md -> md.getParameters().size() == 0).findAny();

				if (!list.isPresent()) {
					throw new CompilationError(node,
							"The method " + methodName + "() is undefined for TypeDefinition " + type);
				}
		
//				TODO move to transformations. use an operator call node
				// replace by a method invocation
				ExpressionNode expr = ensureExpression(node.getChildren().get(0));

				checkAccess(list.get().getDeclaringType(), list.get());
				
				MethodInvocationNode method = new MethodInvocationNode(list.get(), expr);

				method.setTypeVariable(list.get().getReturningType());

				node.getParent().replace(node, method);

			} else if (node instanceof AssignmentNode) {
				AssignmentNode n = (AssignmentNode) node;

				// the left side cannot be a cast
				TypedNode leftNode = n.getLeft();
				if (leftNode instanceof CastNode) {
					AstNode lft = ((CastNode) leftNode).getFirstChild();
					node.replace((AstNode) leftNode, lft);
					leftNode = n.getLeft();
				}

				TypeVariable left = leftNode.getTypeVariable();
				final ExpressionNode rightNode = n.getRight();
				TypeVariable right = rightNode.getTypeVariable();

				if (leftNode instanceof VariableReadNode && rightNode instanceof VariableReadNode) {
					if (((VariableReadNode) leftNode).getVariableInfo() == ((VariableReadNode) rightNode)
							.getVariableInfo()) {
						listener.warn(new CompilerMessage("The assignment to variable "
								+ ((VariableReadNode) leftNode).getName() + " has no effect"));
					}
				}

				if (!typeAssistant.isAssignableTo(right, left).matches()) {

					if (!typeAssistant.isPromotableTo(right, left)) {
						if (left.getTypeDefinition().getName().equals(LenseTypeSystem.Maybe().getName())) {
							// promotable to maybe
							if (!typeAssistant.isPromotableTo(right,
									left.getTypeDefinition().getGenericParameters().get(0))) {
								throw new CompilationError(node, right + " is not assignable to " + left);
							}

							TypeDefinition someTpe = this.getSemanticContext()
									.resolveTypeForName("lense.core.lang.Some", 1).get().getTypeDefinition();

							someTpe = typeAssistant.specify(someTpe, right);

							promoteNodeType(n.getRight(), someTpe);
						} else if (n.getRight() instanceof NumericValue) {

							promoteNodeType(n.getRight(), left);
						} else {
							throw new CompilationError(node, right + " is not assignable to " + left);
						}

					} else {

						promoteNodeType(n.getRight(), left);

					}
				}

				if (leftNode instanceof VariableWriteNode) {
					VariableInfo info = this.getSemanticContext().currentScope()
							.searchVariable(((VariableWriteNode) leftNode).getName());

					if (info.isImutable() && info.isInitialized()) {
						throw new CompilationError(node,
								"Cannot modify the value of an imutable variable or field (" + info.getName() + ")");
					}
					info.setInitialized(true);
				} else if (leftNode instanceof FieldOrPropertyAccessNode) {

					FieldOrPropertyAccessNode fp = (FieldOrPropertyAccessNode) leftNode;


					// is inside constructor ?
					if (fp.getPrimary() != null && fp.getPrimary() instanceof VariableReadNode
							&& ((VariableReadNode) fp.getPrimary()).getName().equals("this")
							&& isInsideConstructor(fp)) {
						throw new CompilationError(node,
								"Invalid access to this. Instance scope is not defined inside constructor. Please use primary constructor instead.");
					}

					if (fp.getKind() == FieldKind.FIELD) {
						
						var field = Optional.ofNullable(this.currentType).flatMap(it -> it.getFieldByName(fp.getName()));
						
						if (field.isEmpty()) {
							
							VariableInfo info = this.getSemanticContext().currentScope()
									.searchVariable(fp.getName());

							if (info == null) {
								throw new CompilationError(node, "Variable or field "
										+ ((FieldOrPropertyAccessNode) leftNode).getName() + " is not defined");
							}
							
							if (info.getDeclaringNode() instanceof FormalParameterNode formal) {
								ensureMutable(node, info);
							}
							if (info.isImutable() && info.isInitialized()) {

								AstNode parent = ((LenseAstNode) leftNode).getParent().getParent().getParent();
								if (!(parent instanceof ConstructorDeclarationNode)) {
									throw new CompilationError(node,
											"Cannot modify the value of an immutable variable or field (" + info.getName()
													+ ")");
								}

							}
							info.setInitialized(true);
						} else {
							// desambiuate with parameter
							VariableInfo info = this.getSemanticContext().currentScope()
									.searchVariable(fp.getName());
							
							if (info != null && info.getDeclaringNode() instanceof FormalParameterNode formal) {
								ensureMutable(node, info);
							} 
						}
						
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
										"Property " + ((FieldOrPropertyAccessNode) leftNode).getName()
												+ " is read only and it cannot be asigned to");

							}

						} else {
							throw new CompilationError(node, "Property "
									+ ((FieldOrPropertyAccessNode) leftNode).getName() + " is not defined in type "
									+ ((TypedNode) fp.getPrimary()).getTypeVariable().getTypeDefinition().getName());
						}

					}

				} else if (leftNode instanceof IndexedPropertyReadNode) {
					IndexedPropertyReadNode a = (IndexedPropertyReadNode) leftNode;

					ArgumentListNode list = new ArgumentListNode(a.getArguments());

					list.add(rightNode);

					list.getLastArgument().setExpectedType(rightNode.getTypeVariable());

					MethodInvocationNode mth = new MethodInvocationNode(a.getAccess(), "set", list);
					mth.setIndexDerivedMethod(true);
					mth.setScanPosition(node.getScanPosition());
					mth.setTypeVariable(LenseTypeSystem.Void());
					mth.setTypeMember(a.getIndexerProperty());

					node.getParent().replace(node, mth);
				} else if (leftNode instanceof VariableReadNode readNode) {
				
					ensureMutable(node, readNode.getVariableInfo());
				
				} 

			} else if (node instanceof TernaryConditionalExpressionNode) {
				TernaryConditionalExpressionNode ternary = (TernaryConditionalExpressionNode) node;

				TypeVariable type = typeAssistant.unionOf(ternary.getThenExpression().getTypeVariable(),
						ternary.getElseExpression().getTypeVariable());

				if (type instanceof UnionType) {
					UnionType unionType = (UnionType) type;

					if (typeAssistant.isAssignableTo(unionType.getLeft(), unionType.getRight()).matches()) {
						type = unionType.getRight(); // TODO promote side
					} else if (typeAssistant.isAssignableTo(unionType.getRight(), unionType.getLeft()).matches()) {
						type = unionType.getLeft(); // TODO promote side
					} else if (typeAssistant.isPromotableTo(unionType.getLeft(), unionType.getRight())) {
						type = unionType.getRight(); // TODO promote side
					} else if (typeAssistant.isPromotableTo(unionType.getRight(), unionType.getLeft())) {
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

				ExpressionNode init = variableDeclaration.getInitializer();

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

				info.setImutable(variableDeclaration.getImutability() == Imutability.Imutable);

				variableDeclaration.setInfo(info);

				if (init != null) {

					info.setInitialized(true);

					VariableRange limits = VariableRange.extractFrom((AstNode) init);

					if (limits.getMin().isPresent()) {
						info.setMininumValue(limits.getMin().get());
					}
					if (limits.getMax().isPresent()) {
						info.setMininumValue(limits.getMax().get());
					}
					info.setIncludeMaximum(limits.isIncludeMax());

					TypeVariable right = init.getTypeVariable();

					if (!typeAssistant.isAssignableTo(right, type).matches()) {
						if (typeAssistant.isPromotableTo(right, type)) {

							if (typeAssistant.isNumber(type.getTypeDefinition()) && init instanceof NumericValue) {
								((NumericValue) init).setTypeVariable(type);
							} else {
								Optional<Constructor> op = typeAssistant.getConstructorByImplicitAndPromotableParameters(type.getTypeDefinition(), true,
												new ConstructorParameter(right));

								NewInstanceCreationNode cn = NewInstanceCreationNode.of(op.get(),
										variableDeclaration.getInitializer());

								if (!type.getGenericParameters().isEmpty()) {

									for (TypeVariable variable : type.getGenericParameters()) {
										cn.getCreationParameters().getTypeParametersListNode()
												.add(new GenericTypeParameterNode(new TypeNode(variable)));
									}

								}

								variableDeclaration.setInitializer(cn);
							}

						} else if (typeAssistant.isTuple(type, 1)) { // TODO
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
						} else if (typeAssistant.isMaybe(type)) {

							TypeDefinition someType = getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1)
									.get().getTypeDefinition();

							var someTypeOfRight = typeAssistant.specify(someType, right);

							this.promoteNodeType((AstNode) init, someTypeOfRight);

						} else {
							promoteNodeType(init, type);

						}
					}
				}

				if (node instanceof FieldDeclarationNode) {
					FieldDeclarationNode f = (FieldDeclarationNode) node;

					LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope()
							.getCurrentType();

					currentType.addField(f.getName(), f.getTypeVariable(), f.getImutability(), f.getVisibility().getVisibility());

					// TODO only is used in constructor
					info.setInitialized(true);
				}
			} else if (node instanceof PropertyDeclarationNode) {
				PropertyDeclarationNode p = (PropertyDeclarationNode) node;

				if (p.getModifier() != null && this.currentType.isImmutable()) {
					throw new CompilationError(p, "Immutable types cannot define modifiers");
				}

				if (p.getAcessor() != null && p.getModifier() != null
						&& p.getAcessor().isImplicit() ^ p.getModifier().isImplicit()) {
					throw new CompilationError(p, "Implicit properties cannot define implementation");
				}

				if (p.getInitializer() != null) {
					ExpressionNode exp = p.getInitializer();

					TypeVariable expType = exp.getTypeVariable();

					TypeVariable propType = p.getType().getTypeVariable();
					if (!typeAssistant.isAssignableTo(expType, propType).matches()) {

						promoteNodeType(exp, propType);

					}
				}

				// auto-abstract if interface
				var kind = this.getSemanticContext().currentScope().getCurrentType().getKind();
				if (kind.isInterface()) {
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
				} else if (kind.isTypeClass()) {
					p.setAbstract(true);

					if (p.getVisibility() == null) {
						p.setVisibility(Visibility.Public);
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

				String typeName = p.getType().getName();
				TypeVariable propertyType = p.getType().getTypeVariable();
				
				if (p.getVisibility() == Visibility.Private) {
					// change to field 
					
					if (p.getModifier() != null && !p.getModifier().isImplicit()) {
						throw new CompilationError(p, "Private properties cannot define modifiers");
					}

					if (p.getAcessor() != null  && !p.getAcessor().isImplicit()) {
						throw new CompilationError(p, "Private properties cannot define acessors");
					}
					
					currentType.addField(p.getName(), propertyType, p.getImutability(), Visibility.Private);
					
				} else {
					LenseTypeDefinition currentType = (LenseTypeDefinition) this.getSemanticContext().currentScope()
							.getCurrentType();

					
					VariableInfo genericParameter = this.getSemanticContext().currentScope().searchVariable(typeName);
					
					if (genericParameter != null && genericParameter.isTypeVariable()) {
						// type i a generic variable T
						List<TypeVariable> parameters = currentType.getGenericParameters();
						Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);

						if (!opIndex.isPresent()) {
							throw new CompilationError(node,
									typeName + " is not a valid generic parameter for type " + currentType.getName());
						}

						int index = opIndex.get();

						propertyType = new DeclaringTypeBoundedTypeVariable(currentType, index, typeName,
								parameters.get(index).getVariance());

					} else if (!propertyType.getGenericParameters().isEmpty()){
						
						var list = propertyType.getGenericParameters().stream().filter(it -> it.getSymbol().isPresent()).toList();

						if (list.size() == 1) {
							
							var param = propertyType.getGenericParameters().get(0);
							if (param.getSymbol().isPresent()) {
								Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(param.getSymbol().get());

								if (!opIndex.isPresent()) {
									throw new CompilationError(node,
											param.getSymbol().get() + " is not a valid generic parameter for type " + currentType.getName());
								}
								
								int index = opIndex.get();
								
								
								propertyType = new GenericTypeBoundToDeclaringTypeVariable(propertyType.getTypeDefinition(), currentType, index, param.getSymbol().get(), param.getVariance());
							}
							
						} else if (list.size() > 1){
							throw new UnsupportedOperationException("cannot handle more than one generic parameter");
						}
						
						
					}

					TypeMember property;
					if (p.isIndexed()) {

						lense.compiler.type.variable.TypeVariable[] params = new lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode) p)
								.getIndexes().getChildren().size()];
						int i = 0;
						for (FormalParameterNode var : ((IndexerPropertyDeclarationNode) p).getIndexes()
								.getChildren(FormalParameterNode.class)) {
							params[i++] = var.getTypeNode().getTypeParameter();
						}

						property = currentType.addIndexer(propertyType, p.getVisibility(), p.getAcessor() != null, p.getModifier() != null,
								params);

					} else {
						property = currentType.addProperty(p.getName(), propertyType,p.getVisibility(), p.getAcessor() != null,
								p.getModifier() != null);

					}

					// verify duplication

					if (!this.declaredMembers.add(property)) {
						throw new CompilationError(node, "The property '" + property.getName()
								+ "' was already declared in type " + this.currentType.getName());
					}
					
					List<TypeMember> superProperties = searchSuperProperties(currentType, property.getName());
					
					if (superProperties.size() == 1) {
						var found = superProperties.get(0);
						if (found.getDeclaringType().getKind().isInterface()) {
							if (property.getVisibility().isLessVisibleThan(found.getVisibility())) {
								throw new CompilationError(node,
										"The property " + property.getName() + " in type " + this.currentType.getName()
												+ " cannot declare the property in "
												+ found.getDeclaringType().getName()
												+ " with less visibility");
							}
						} else {
							throw new CompilationError(node,
									"The property " + property.getName() + " in type " + this.currentType.getName()
											+ " cannot be overwriten in "
											+ found.getDeclaringType().getName());
						}
					
					}
				}
				
			

			} else if (node instanceof IndexedPropertyReadNode) {
				IndexedPropertyReadNode m = (IndexedPropertyReadNode) node;

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

					ExpressionNode indexArgument = (ExpressionNode) m.getArguments().getFirstArgument().getFirstChild();
					Optional<Integer> index = asConstantNumber(indexArgument);
					if (index.isPresent()) {

						int maxIndex = countTupleSize(methodOwnerType);

						Optional<Method> headMethod = typeAssistant.getMethodBySignature(LenseTypeSystem.Tuple(), new MethodSignature("head"));
						Optional<Method> tailMethod = typeAssistant.getMethodBySignature(LenseTypeSystem.Tuple(), new MethodSignature("tail"));

						if (index.get() == 0) {
							MethodInvocationNode invoke = new MethodInvocationNode(headMethod.get(),
									ensureExpression(m.getAccess()));
							invoke.setTupleAccessMethod(true);

							node.getParent().replace(node, invoke);
							invoke.setTypeVariable(methodOwnerType.getGenericParameters().get(0));

							return;
						} else if (index.get() < maxIndex) {
							MethodInvocationNode previous = new MethodInvocationNode(tailMethod.get(),
									ensureExpression(m.getAccess()));
							previous.setTupleAccessMethod(true);
							previous.setTypeVariable(methodOwnerType.getGenericParameters().get(1));

							for (int i = 0; i < index.get() - 1; i++) {
								MethodInvocationNode current = new MethodInvocationNode(tailMethod.get(), previous);
								current.setTypeVariable(
										previous.getTypeVariable().getGenericParameters().get(1).getUpperBound());
								current.setTupleAccessMethod(true);
								previous = current;
							}

							TypeVariable upperbound = previous.getTypeVariable().getGenericParameters().get(0)
									.getUpperBound();

							MethodInvocationNode invoke = new MethodInvocationNode(headMethod.get(), previous);
							invoke.setTypeVariable(upperbound);
							invoke.setTupleAccessMethod(true);

							CastNode cast = new CastNode(invoke, upperbound.getTypeDefinition());

							node.getParent().replace(node, cast);

							return;
						} else {
							MethodInvocationNode previous = new MethodInvocationNode(tailMethod.get(),
									ensureExpression(m.getAccess()));
							previous.setTupleAccessMethod(true);
							previous.setTypeVariable(methodOwnerType.getGenericParameters().get(1));

							for (int i = 0; i < index.get() - 2; i++) {
								MethodInvocationNode current = new MethodInvocationNode(tailMethod.get(), previous);
								current.setTypeVariable(previous.getTypeVariable().getGenericParameters().get(1));
								current.setTupleAccessMethod(true);
								previous = current;
							}

							MethodInvocationNode current = new MethodInvocationNode(tailMethod.get(), previous);
							current.setTypeVariable(previous.getTypeVariable());
							current.setTupleAccessMethod(true);
							TypeVariable previousValueType = previous.getTypeVariable().getGenericParameters().get(0);
							previous = current;

							MethodInvocationNode invoke = new MethodInvocationNode(headMethod.get(), previous);
							invoke.setTupleAccessMethod(true);
							invoke.setTypeVariable(previousValueType);

							CastNode cast = new CastNode(invoke, previousValueType.getTypeDefinition());
							cast.setTupleAccessMethod(true);

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

					Optional<IndexerProperty> indexer = typeAssistant.getIndexerPropertyByTypeArray(methodOwnerType, signatureTypes);

					if (!indexer.isPresent()) {
						throw new CompilationError(node, "No indexer ["
								+ Stream.of(signatureTypes).map(t -> t.toString()).collect(Collectors.joining(","))
								+ "] is defined for type " + methodOwnerType);
					}

					m.setIndexerProperty(indexer.get());
					TypeVariable rawReturnType = indexer.get().getReturningType();

					var type = RangeTypeVariable.upTo(rawReturnType.getSymbol(),
							rawReturnType.getVariance(), rawReturnType.getTypeDefinition());
					m.setTypeVariable(type);

					// m.setTypeVariable(rawReturnType);

//                    CastNode cast = new CastNode(m , type);
//                    m.getParent().replace(m, cast);
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

	
				Optional<Field> field;
				if (fieldOwnerType.equals(currentType)) {

					field = resolveFieldPropertyOrVariableName(node, m, currentType, fieldOwnerType, name);

				} else {

					TypeDefinition def = this.getSemanticContext().ensureNotFundamental(fieldOwnerType.getTypeDefinition());
					field = def.getFieldByName(name);

					if (!field.isPresent()) {

						Optional<Property> property = def.getPropertyByName(name);

						if (!property.isPresent()) {
							if (!typeAssistant.isAssignableTo(def, LenseTypeSystem.Maybe()).matches()) {
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
							TypeVariable finalType = typeAssistant.specify(LenseTypeSystem.Maybe(),
									field.get().getReturningType());
							TypeDefinition mappingFunction = typeAssistant.specify(LenseTypeSystem.Function(2),
									innerType, field.get().getReturningType());

							TypeVariable type = mappingFunction;
							NewInstanceCreationNode newObject = NewInstanceCreationNode.of(type);

							newObject.getCreationParameters().getTypeParametersListNode()
									.add(new GenericTypeParameterNode(new TypeNode(type)));

							ArgumentListItemNode arg = new ArgumentListItemNode(0, newObject);
							arg.setExpectedType(newObject.getTypeVariable());

							// TODO this will resolve the correct m
							Optional<Method> method = typeAssistant.getMethodBySignature(
									finalType.getTypeDefinition(), new MethodSignature("map", new MethodParameter(newObject.getTypeVariable())));

							MethodInvocationNode transform = new MethodInvocationNode(method.get(),
									ensureExpression(m.getPrimary()), arg
							// TODO
							// lambda
							);

							m.getParent().replace(m, transform); // this operation
							// will
							// nullify the
							// transform.type.
							m.setTypeVariable(finalType); // set it again
							transform.setTypeVariable(finalType); // set it again
																	// again
						} else {
							m.setTypeVariable(property.get().getReturningType());
							m.setKind(FieldOrPropertyAccessNode.FieldKind.PROPERTY);
						}
					} else {
						m.setTypeVariable(field.get().getReturningType());
						m.setKind(FieldOrPropertyAccessNode.FieldKind.FIELD);
					}
				}
				

				
				if (field.isPresent()) {
					switch (field.get().getVisibility()) {
					case Private:
						if (!currentType.equals(fieldOwnerType)) {
							throw new CompilationError(node, "Invalid access to private field" );
						}
						break;
					case Protected:
						if (currentType.equals(fieldOwnerType) || currentType.equals(fieldOwnerType)) {
							break;
						}
						throw new CompilationError(node, "Invalid access to protected field" );
					default:
						//no-op
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
					}
				}

			} else if (node instanceof MethodInvocationNode m) {
				
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

					methodOwnerType = currentType;
				
				} else if (access instanceof TypeOfInvocation) {
					var typeOf = (TypeOfInvocation) access;
					var typeOfType = this.getSemanticContext().typeForName( typeOf.getTypeNode()).getTypeDefinition();
					methodOwnerType = typeOfType;
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

				TypeDefinition def = this.getSemanticContext().ensureNotFundamental(methodOwnerType.getTypeDefinition());

				if (def.getMembers().isEmpty()) {
					// no method exist, so no search will work
					
					throw new TypeMembersNotLoadedError(node, def.getName());
				}
				
				Optional<Method> method = resolveMethod(name, def, m.getCall());

				if (method.isPresent()) {
					checkAccess(def, method.get());
					applyMethodCall(node, m, method.get());
				} else {

					// TODO consider named parameters for enhancements

					MethodParameter[] parameters = asMethodParameters(m.getCall().getArguments());

					MethodSignature signature = new MethodSignature(name, parameters);

					// search in enhancements

					if (!this.enhancements.isEmpty()) {

						List<Method> found = new LinkedList<>();

						for (Map.Entry<TypeVariable, List<TypeDefinition>> entry : this.enhancements.entrySet()) {

							if (typeAssistant.isAssignableTo(methodOwnerType, entry.getKey()).matches()) {

								for (TypeDefinition enhancement : entry.getValue()) {
									method = typeAssistant.getMethodByPromotableSignature(enhancement, signature);
									if (method.isPresent()) {
										found.add(method.get());
									}
								}

							}
						}

						if (found.size() == 1) {
							// set method return type

							applyMethodCall(node, m, found.get(0));

							m.setEnchamentCall(true);

							return;
						} else if (found.size() > 1) {
							throw new CompilationError(node,
									"More than one enhancement matches call to '" + name + "' in type '" + def.getName()
											+ "' with arguments " + Arrays.toString(parameters)
											+ ". Please, desambiguate.");
						}

					}

					throw new CompilationError(node, "There is no method named '" + name + "' in type '" + def.getName()
							+ "' with arguments " + Arrays.toString(parameters) + " nor an enchament matches.");

				}


			} else if (node instanceof NewInstanceCreationNode) {

				if (node instanceof lense.compiler.ast.LiteralCreation) {
					return;
				}
				NewInstanceCreationNode n = (NewInstanceCreationNode) node;

				TypeDefinition def = n.getTypeNode().getTypeVariable().getTypeDefinition();
				
				
				checkAccess(def);

				ConstructorParameter[] parameters = n.getArguments() == null ? new ConstructorParameter[0]
						: asConstructorParameters(n.getArguments());

				List<Match<Constructor>> constructors = typeAssistant.getConstructorByName(def,n.getName(), parameters);
				Optional<Constructor> constructor;

				if (constructors.isEmpty()) {
					constructor = n.getName() == null 
							? typeAssistant.getConstructorByPromotableParameters(def,parameters)
							: typeAssistant.getConstructorByNameAndPromotableParameters(def,n.getName(), parameters);

					if (!constructor.isPresent()) {

						if (!this.enhancements.isEmpty() && n.getName() != null) {

							// only constructors with name can be enchanced
							LinkedList<Constructor> found = new LinkedList<>();

							for (Map.Entry<TypeVariable, List<TypeDefinition>> entry : this.enhancements.entrySet()) {

								if (typeAssistant.isAssignableTo(def, entry.getKey()).matches()) {

									for (TypeDefinition enhancement : entry.getValue()) {

										var enhancedConstructor = typeAssistant 
												.getConstructorByNameAndPromotableParameters(enhancement,n.getName(), parameters);

										if (enhancedConstructor.isPresent()) {
											found.add(enhancedConstructor.get());
										}
									}

								}
							}

							if (found.size() == 1) {
								// set method return type

								constructor = Optional.of(found.getFirst());

							} else if (found.size() > 1) {
								throw new CompilationError(node,
										"More than one enhancement matches call to constructor '" + n.getName()
												+ "' in type '" + def.getName() + "' with arguments "
												+ Arrays.toString(parameters) + ". Please, desambiguate");
							}

						} else {
							throw new CompilationError(n,
									"Constructor " + def.getName() + "(" + Stream.of(parameters)
											.map(cp -> cp.toString()).collect(Collectors.joining(","))
											+ ") is not defined");
						}

					}
				} else if (constructors.size() == 1) {

					constructor = constructors.stream().findFirst().map(c -> c.getCandidate());

				} else {

					constructor = constructors.stream().findFirst().map(c -> c.getCandidate());
				}

				checkAccess(def, constructor.get());
				
				n.setConstructor(constructor.get());

				if (n.getArguments() != null) {

					List<CallableMemberMember<Constructor>> methodParameters = n.getConstructor().getParameters();
					var pureArguments = n.getArguments().getChildren(ArgumentListItemNode.class, it -> !it.isReificiationArgument());
					
					if (methodParameters.size() != pureArguments.size()) {
						throw new CompilationError(node, "Argument count does not match parameters count");
					}

					Map<String, Integer> orderedParamNames = new LinkedHashMap<>();

					for (int i = 0; i < methodParameters.size(); i++) {
						ConstructorParameter param = (ConstructorParameter) methodParameters.get(i);
						orderedParamNames.put(param.getName(), i);
					}

					ReorderArguments(n, orderedParamNames);

					for (int i = 0; i < methodParameters.size(); i++) {
						ConstructorParameter param = (ConstructorParameter) methodParameters.get(i);
						ArgumentListItemNode arg = pureArguments.get(i);
						arg.setExpectedType(param.getType());

						promoteNodeType(arg.getFirstChild(), param.getType());
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
				VariableInfo returnVariable = this.getSemanticContext().currentScope()
						.searchVariable("@returnOfMethod");

				TypeVariable union = typeAssistant.unionOf(returnVariable.getTypeVariable(), n.getTypeVariable());

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
				TypeVariable returnType = this.getSemanticContext().ensureNotFundamental(m.getReturnType().getTypeParameter());

				var kind = this.currentType != null
							? this.currentType.getKind()
							: this.getSemanticContext().currentScope().getCurrentType().getKind();
				
				if (kind.isInterface() || kind.isTypeClass()) {
					m.setVisibility(Visibility.Public);
					m.setAbstract(true);
				}
				
				
				if (m.isNative() && m.getBlock() != null) {
					throw new CompilationError(node,
							"Method " + m.getName() + " cannot be native and have and implementation");
				}

				if (m.isAbstract() && m.getBlock() != null) {
					throw new CompilationError(node,
							"Method " + m.getName() + " cannot be abstract and have and implementation");
				} else if (!m.isAbstract() && m.getBlock() == null && !m.isNative()) {
					throw new CompilationError(node, "Method " + m.getName() + " must have and implementation");
				}

				if (!m.isAbstract()) {

					if (!m.getReturnType().needsInference()) {
						if (returnType.getTypeDefinition().equals(VOID)) {
							VariableInfo variable = this.getSemanticContext().currentScope()
									.searchVariable("@returnOfMethod");

							if (variable != null
									&& !typeAssistant.isAssignableTo(variable.getTypeVariable(), VOID).matches()) {
								throw new CompilationError(node, "Method " + m.getName() + " cannot return a value");
							}
						} else {
							VariableInfo variable = this.getSemanticContext().currentScope()
									.searchVariable("@returnOfMethod");

							TypeVariable typeVariable = this.getSemanticContext().ensureNotFundamental(variable.getTypeVariable());
							if (!m.isNative()
									&& (variable == null || typeAssistant.isNothing(typeVariable))) {

								TypeDefinition currentType = this.getSemanticContext().currentScope().getCurrentType();
								if (currentType.getKind() == LenseUnitKind.Class) {
									throw new CompilationError(node,
											"Method " + m.getName() + " must return a result of " + returnType);
								}
							}

							if (!typeAssistant.isAssignableTo(typeVariable, returnType).matches()) {

								if (!typeAssistant.isPromotableTo(typeVariable, returnType)) {
									throw new CompilationError(node,
											typeVariable + " is not assignable to " + returnType
													+ " in the return of method " + m.getName());
								} else {
									// TODO promote

									Collection<ReturnNode> allReturns = m.getBlock().findAllReturnNodes();

									if (allReturns.isEmpty()) {
										throw new CompilationError(node,
												typeVariable + " no return instruction found");
									}

								
									for (ReturnNode rn : allReturns) {
										
										var returnVarialbType = rn.getTypeVariable();
										
										var match = typeAssistant.isAssignableTo(returnVarialbType, returnType);
										
										if (!match.isExact()) {
											Constructor op = optionalConstructor(returnType,
													new ConstructorParameter(returnVarialbType));

											
											NewInstanceCreationNode cn = NewInstanceCreationNode.of(op,
													rn.getChildren().get(0));
											cn.getCreationParameters().getTypeParametersListNode()
													.add(new GenericTypeParameterNode(new TypeNode(returnType)));

											ReturnNode nr = new ReturnNode();
											nr.add(cn);

											rn.getParent().replace(rn, nr);
										}
										
										
									}

								}
							}

						}

					}

				}

			} else if (node instanceof ClassTypeNode) {

				ClassTypeNode t = (ClassTypeNode) node;
				if (t.getInterfaces() != null) {

					for (AstNode n : t.getInterfaces().getChildren()) {
						TypeNode tn = (TypeNode) n;
						TypeDefinition typeVariable = this.getSemanticContext().ensureNotFundamental(tn.getTypeVariable().getTypeDefinition());
						if (!typeVariable.getKind().isInterface()) {
							if (!typeVariable.getKind().isTypeClass() || !t.getSimpleName().contains("$$Type")) {
								throw new CompilationError(t,
										t.getFullname() + " cannot implement " + typeVariable.getName() + " because "
												+ typeVariable.getName() + " it is a " + typeVariable.getKind()
												+ " and not an interface");
							} 
					
						}
					}
				}

				if (t.isAlgebric() && !t.isAbstract()) {
					throw new CompilationError(t, t.getFullname()
							+ " is algebric but is not marked abstract. Make it abstract or remove children types declarations.");
				}

				t.isAsStringDefined(typeAssistant.getMethodBySignature(t.getTypeDefinition(), new MethodSignature("asString"))
						.map(m -> m.getSuperMethod() == null).orElse(false));
				t.isHashValueDefined(typeAssistant.getMethodBySignature(t.getTypeDefinition(),new MethodSignature("hashValue"))
						.map(m -> m.getSuperMethod() == null).orElse(false));
				t.setEqualsToDefined(typeAssistant.getMethodBySignature(t.getTypeDefinition(),new MethodSignature("equalsTo", new MethodParameter(ANY)))
						.map(m -> m.getSuperMethod() == null).orElse(false));

				if (t.getKind().isValue()) {

					if (!t.isEqualsToDefined() && !t.isHashValueDefined()) {
						createSynteticEqualsAndHash(t);
					} else if ((!t.isEqualsToDefined() && t.isHashValueDefined())
							|| (t.isEqualsToDefined() && !t.isHashValueDefined())) {
						throw new CompilationError(t, "Methods equalsTo and hashValue must be overrided together. ");
					}

					if (!t.isAsStringDefined()) {
						createSynteticAsString(t);
					}

				} else if ((!t.isEqualsToDefined() && t.isHashValueDefined())
						|| (t.isEqualsToDefined() && !t.isHashValueDefined())) {
					throw new CompilationError(t, "Methods equalsTo and hashValue must be overrided together. ");
				}

			} else if (node instanceof ConditionalStatement) {

				TypeVariable conditionType = ((ConditionalStatement) node).getCondition().getTypeVariable();

				if (!this.typeAssistant.isBoolean(conditionType)) {
					throw new CompilationError(node,
							"Condition must be a Boolean value, found " + conditionType.getTypeDefinition());
				}
			} else if (node instanceof ForEachNode) {
				ForEachNode n = (ForEachNode) node;

				if (!typeAssistant.isAssignableTo(n.getContainer().getTypeVariable(), LenseTypeSystem.Iterable())
						.matches()) {

					// verify correct type loading

					TypeDefinition def = n.getContainer().getTypeVariable().getTypeDefinition();
					TypeVariable g = this.getSemanticContext()
							.resolveTypeForName(def.getName(), def.getGenericParameters().size()).get();

					if (!typeAssistant.isAssignableTo(g, LenseTypeSystem.Iterable()).matches()) {
						throw new CompilationError(node,
								"Can only iterate over an instance of " + LenseTypeSystem.Iterable());
					}

				}

				if (!typeAssistant.isAssignableTo(
						n.getContainer().getTypeVariable().getGenericParameters().get(0).getUpperBound(),
						n.getVariableDeclarationNode().getTypeVariable()).matches()) {
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
				if (!typeAssistant.isAssignableTo(exceptionType, LenseTypeSystem.Exception()).matches()) {
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

	
	private void ensureMutable(AstNode node, VariableInfo info) {
		if (info.getDeclaringNode() instanceof VariableDeclarationNode declaration ) {
			if (declaration.getImutability() == Imutability.Imutable) {
				throw new CompilationError(node,
						"Cannot modify the value of an immutable variable (" + info.getName() + ")");
			}
		} else if (info.getDeclaringNode() instanceof FormalParameterNode formalParameter 
				&& formalParameter.getImutability() == Imutability.Imutable) {
			throw new CompilationError(node,
					"Cannot modify the value of an immutable parameter (" +info.getName() + ")");
		}	
	}

	private List<TypeMember> searchSuperProperties(LenseTypeDefinition type, String name) {
		var result = new LinkedList<TypeMember>();
		
		searchSuperProperties(type, name, result);
		
		return result;
	}
	
	private void searchSuperProperties(TypeDefinition type, String name, List<TypeMember> result) {
		 
		if (typeAssistant.isAny(type)) {
			return;
		}
		
		result.addAll(type.getSuperDefinition().getAllMembers().stream().filter(m -> m.isProperty() && name.equals(m.getName()))
		.collect(Collectors.toList()));
		
		searchSuperProperties(type.getSuperDefinition(), name, result);
		
		result.addAll(type.getInterfaces().stream().flatMap(i -> i.getAllMembers().stream()).filter(m -> m.isProperty() && name.equals(m.getName()))
				.collect(Collectors.toList()));
		
		for ( var i : type.getInterfaces()) {
			searchSuperProperties(i, name, result);
		}
		
	}

	private void checkAccess(TypeDefinition def, CallableMember<? extends CallableMember> callable) {
		checkAccess(def);
		var myname = this.getSemanticContext().getCurrentPackageName();
		if (!def.getPackageName().equals(myname)) {
			if (callable.getVisibility().equals(Visibility.Protected)){
				if (!typeAssistant.isSuper(def, this.currentType)) {
					throw new CompilationError( callable.getName() + " has protected access in " + def.getName());
				}
			} else if (callable.getVisibility().equals(Visibility.Private)){
				throw new CompilationError( callable.getName() + " has private access in " + def.getName());
			}
		}
	}

	private void checkAccess(TypeDefinition def) {
		var myname = this.getSemanticContext().getCurrentPackageName();
		if (!def.getPackageName().equals(myname) && !def.getVisibility().equals(Visibility.Public)) {
			throw new CompilationError(def.getName() + " is not public in " + def.getPackageName() + " and cannot be accessed from outside its package");
			
		}
	}
	


	private boolean isInsideConstructor(FieldOrPropertyAccessNode leftNode) {
		AstNode n = leftNode;
		while (n.getParent() != null) {
			n = n.getParent();

			if (n instanceof ConstructorDeclarationNode) {
				return true;
			} else if (n instanceof MethodDeclarationNode) {
				return false;
			} else if (n instanceof ClassBodyNode) {
				return false;
			}
		}

		return false;

	}

	private void createSynteticAsString(ClassTypeNode t) {
		// TODO Auto-generate asString

	}

	private void createSynteticEqualsAndHash(ClassTypeNode t) {
		// define equals based on all properties

		MethodDeclarationNode equals = new MethodDeclarationNode();
		equals.setName("equalsTo");
		equals.setOverride(true);
		equals.setReturnType(new TypeNode(LenseTypeSystem.Boolean()));
		equals.setVisibility(Visibility.Public);

		ParametersListNode parameters = new ParametersListNode();
		parameters.add(new FormalParameterNode("other", LenseTypeSystem.Any()));
		equals.setParameters(parameters);

		VariableInfo varInfo = this.getSemanticContext().currentScope().defineTypeVariable("other",
				LenseTypeSystem.Any(), equals);

		BlockNode block = new BlockNode();
		equals.setBlock(block);

		ReturnNode r = new ReturnNode();
		block.add(r);

		InstanceOfNode isOf = new InstanceOfNode();
		isOf.add(new VariableReadNode("other", varInfo));
		isOf.add(new TypeNode(t.getTypeDefinition()));

		Deque<ExpressionNode> expressions = new LinkedList<>();
		t.getTypeDefinition().getAllMembers().stream().filter(m -> m instanceof Property).map(m -> (Property) m)
				.forEach(p -> {

					ComparisonNode c = new ComparisonNode(ComparisonNode.Operation.EqualTo);

					FieldOrPropertyAccessNode pa = new FieldOrPropertyAccessNode(p.getName());
					pa.setKind(FieldKind.PROPERTY);
					pa.setTypeVariable(p.getReturningType());

					c.add(pa);

					FieldOrPropertyAccessNode pb = new FieldOrPropertyAccessNode(p.getName());
					pb.setKind(FieldKind.PROPERTY);
					pb.setPrimary(new CastNode(new VariableReadNode("other"), t.getTypeDefinition()));
					pb.setTypeVariable(p.getReturningType());

					c.add(pb);

					expressions.add(c);

				});

		if (!expressions.isEmpty()) {

			BooleanOperatorNode and = new BooleanOperatorNode(BooleanOperation.LogicShortAnd);
			and.add(isOf);

			r.setValue(and);

			BooleanOperatorNode previousAnd = and;

			while (expressions.size() != 1) {
				and = new BooleanOperatorNode(BooleanOperation.LogicShortAnd);
				previousAnd.add(and);
				and.add(expressions.removeFirst());
				previousAnd = and;
			}

			previousAnd.add(expressions.removeFirst());

		} else {
			r.setValue(isOf);
		}

		t.getBody().add(equals);

		t.setEqualsToDefined(true);

		// hashValue

		TypeVariable hashValueType = this.getSemanticContext().resolveTypeForName("lense.core.lang.HashValue", 0).get();

		MethodDeclarationNode hash = new MethodDeclarationNode();
		hash.setName("hashValue");
		hash.setOverride(true);
		hash.setReturnType(new TypeNode(hashValueType));
		hash.setVisibility(Visibility.Public);

		parameters = new ParametersListNode();
		hash.setParameters(parameters);

		block = new BlockNode();
		hash.setBlock(block);

		r = new ReturnNode();
		block.add(r);

		NewInstanceCreationNode newConcat = new NewInstanceCreationNode();
		newConcat.setTypeNode(new TypeNode(hashValueType));

		ExpressionNode access = newConcat;
		Iterator<Property> it = t.getTypeDefinition().getAllMembers().stream().filter(m -> m instanceof Property)
				.map(m -> (Property) m).iterator();

		while (it.hasNext()) {
			Property p = it.next();

			FieldOrPropertyAccessNode pa = new FieldOrPropertyAccessNode(p.getName());
			pa.setKind(FieldKind.PROPERTY);
			pa.setTypeVariable(p.getReturningType());

			MethodInvocationNode h = new MethodInvocationNode(pa, "hashValue");
			h.setTypeVariable(hashValueType);

			access = new MethodInvocationNode(access, "concat", new ArgumentListItemNode(0, h));
			access.setTypeVariable(hashValueType);
		}

		r.setValue(access);

		t.getBody().add(hash);

		t.setHashValueDefined(true);
	}

	private Optional<TypeVariable> tryPromoteEnds(AstNode node,  ExpressionNode left,
			ExpressionNode right) {

		Optional<TypeVariable> oleft = Optional.ofNullable(left).map(s -> s.getTypeVariable());
		Optional<TypeVariable> oright = Optional.ofNullable(right).map(s -> s.getTypeVariable());

		if (oleft.isPresent() && oright.isPresent()) {
			TypeVariable tleft = oleft.get();
			TypeVariable tright = oright.get();

			TypeDefinition leftDef = this.getSemanticContext()
					.resolveTypeForName(tleft.getTypeDefinition().getName(), tleft.getGenericParameters().size()).get()
					.getTypeDefinition();
			TypeDefinition rightDef = this.getSemanticContext()
					.resolveTypeForName(tright.getTypeDefinition().getName(), tright.getGenericParameters().size())
					.get().getTypeDefinition();

			if (oleft.equals(oright)) {
				return oleft;
			} else if (typeAssistant.isPromotableTo(leftDef, rightDef)) {

				// cast left to right

				promoteNodeType(left, tright);

				return Optional.of(tright);
			} else if (typeAssistant.isPromotableTo(rightDef, leftDef)) {
				// cast right to left

				promoteNodeType(right, tleft);

				return Optional.of(tleft);
			} else {
				return Optional.empty();
				// throw new CompilationError(node, "Cannot create interval from " + left + " to
				// " + right);
			}
		} else if (oleft.isPresent()) {
			return oleft;
		} else if (oright.isPresent()) {
			return oright;
		} else {
			return Optional.empty();
		}

	}


	private Optional<Method> resolveMethod(String methodName, TypeDefinition def, ArgumentListHolder holder) {

		ArgumentListNode args = holder.getArguments();

		Set<String> mamedParams = new HashSet<>();

		List<ArgumentListItemNode> arguments = args.getChildren(ArgumentListItemNode.class);
		for (ArgumentListItemNode arg : arguments) {
			if (arg.getName().isPresent()) {
				if (!mamedParams.add(arg.getName().get())) {
					throw new CompilationError(arg, "Duplicated named parameter '" + arg.getName().get()
							+ "'. Each parameter can only be set once.");
				}
			} else if (!mamedParams.isEmpty()) {
				throw new CompilationError(arg,
						"All named parameters must be set after positional parameter. Parameter '" + arg.getName().get()
								+ "' cannot be set at this position.");
			}
		}

		if (mamedParams.isEmpty()) {
			// purely positional

			MethodParameter[] parameters = asMethodParameters(args);

			MethodSignature signature = new MethodSignature(methodName, parameters);

			Optional<Method> method = typeAssistant.getMethodBySignature(def,signature);

			if (method.isPresent()) {
				return method;
			}

			return typeAssistant.getMethodByPromotableSignature(def,signature);

		} else {
			List<Method> possibleMethods = def.getAllMembers().stream().filter(it -> it.isMethod())
					.map(it -> (Method) it).filter(it -> it.getName().equals(methodName))
					.filter(it -> it.getParameters().size() == args.getChildren().size()).collect(Collectors.toList());

			outter: for (Method m : possibleMethods) {

				Set<String> set = new HashSet<>();
				Map<String, Integer> orderedParamNames = new LinkedHashMap<>();

				int index = 0;

				Iterator<CallableMemberMember<Method>> itParams = m.getParameters().iterator();
				Iterator<ArgumentListItemNode> itArgs = args.getChildren(ArgumentListItemNode.class).iterator();
				while (itParams.hasNext()) {
					ArgumentListItemNode arg = itArgs.next();
					MethodParameter p = (MethodParameter) itParams.next();
					set.add(p.getName());
					orderedParamNames.put(p.getName(), index++);

					if (!arg.getName().isPresent()) {
						// positional. must match parameter
						if (!typeAssistant
								.isAssignableTo(((TypedNode) arg.getFirstChild()).getTypeVariable(), p.getType())
								.matches()
								&& !typeAssistant.isPromotableTo(((TypedNode) arg.getFirstChild()).getTypeVariable(),
										p.getType())) {
							continue outter;
						}
					}
				}

				for (String name : mamedParams) {
					if (!set.contains(name)) {
						continue outter;
					}
				}

				// compatible method
				// TODO generate function calls in source call order before reordering

				AstNode topNode = resolveTopNode((AstNode) holder);

				ListIterator<ArgumentListItemNode> iterator = arguments.listIterator(arguments.size());
				while (iterator.hasPrevious()) {
					ArgumentListItemNode arg = iterator.previous();

					ExpressionNode exp = (ExpressionNode) arg.getFirstChild();

					if (arg.getName().isPresent() && !(exp instanceof LiteralExpressionNode)) {

						String variableName = "$" + arg.getScanPosition().getLineNumber() + "$" + arg.getName().get();

						VariableDeclarationNode declare = new VariableDeclarationNode(variableName,
								exp.getTypeVariable(), exp);

						VariableInfo varDef = this.getSemanticContext().currentScope().defineVariable(variableName,
								exp.getTypeVariable(), topNode.getParent().getParent());

						VariableReadNode read = new VariableReadNode(variableName, varDef);

						arg.replace(exp, read);

						topNode.getParent().addBefore(topNode, declare);
					}
				}

				ReorderArguments(holder, orderedParamNames);

				return Optional.of(m);
			}

			return Optional.empty();
		}

	}

	private AstNode resolveTopNode(AstNode holder) {

		if (holder.getParent() instanceof BlockNode) {
			return holder;
		}

		return resolveTopNode(holder.getParent());
	}

	private void applyMethodCall(AstNode node, MethodInvocationNode m, Method mthd) {

		m.setTypeMember(mthd);
		m.setTypeVariable(mthd.getReturningType());

		List<CallableMemberMember<Method>> methodParameters = mthd.getParameters();
		if (methodParameters.size() != m.getCall().getArguments().getChildren().size()) {
			throw new CompilationError(m.getCall(), "Argument count does not match parameters count");
		}

		Map<String, List<ArgumentListItemNode>> freeArguments = new HashMap<>();

		for (int i = 0; i < methodParameters.size(); i++) {
			MethodParameter param = (MethodParameter) methodParameters.get(i);

			// math expected argument with method parameter type
			ArgumentListItemNode arg = m.getCall().getArguments().getChildren(ArgumentListItemNode.class).get(i);
			arg.setExpectedType(param.getType());

			TypedNode value = (TypedNode) arg.getChildren().get(0);

			// check assignability
			if (!typeAssistant.isAssignableTo(value.getTypeVariable(), param.getType()).matches()) {

				if (!typeAssistant.isPromotableTo(value.getTypeVariable(), param.getType())) {
					throw new CompilationError(node,
							"Cannot assign " + value.getTypeVariable().getTypeDefinition().getName() + " to "
									+ param.getType().getTypeDefinition().getName());
				}

				promoteNodeType(arg.getChildren().get(0), param.getType());
			}

			if (param.isMethodTypeBound()) {
				addFreeTypes(methodParameters, freeArguments, param.getType(), arg);
			}

		}

		Map<String, TypeVariable> boundedTypes = new HashMap<>();

		for (Map.Entry<String, List<ArgumentListItemNode>> entry : freeArguments.entrySet()) {

			if (entry.getValue().size() > 1) {
				TypeVariable previous = ((TypedNode) entry.getValue().get(0).getFirstChild()).getTypeVariable();

				for (int i = 1; i < entry.getValue().size(); i++) {
					TypeVariable current = ((TypedNode) entry.getValue().get(i).getFirstChild()).getTypeVariable();

					if (!typeAssistant.isPromotableTo(current, previous)) {

						if (typeAssistant.isPromotableTo(previous, current)) {
							previous = current;
						} else {
							throw new CompilationError(node,
									"Types " + previous.getTypeDefinition().getName() + " and "
											+ current.getTypeDefinition().getName() + " cannot be bound to free type "
											+ entry.getKey());
						}

					}
				}

				boundedTypes.put(entry.getKey(), previous);

			} else {
				TypeVariable previous = ((TypedNode) entry.getValue().get(0).getFirstChild()).getTypeVariable();
				boundedTypes.put(entry.getKey(), previous);
			}
		}

		m.setBoundedTypes(boundedTypes);
	}

	private void ReorderArguments(ArgumentListHolder m, Map<String, Integer> orderedParamNames) {
		List<ArgumentListItemNode> argList = new ArrayList<>(m.getArguments().getChildren(ArgumentListItemNode.class));

		if (argList.size() > 1) {
			Collections.sort(argList, (a, b) -> {

				if (!a.getName().isPresent() && !b.getName().isPresent()) {
					return 0;
				} else if (!a.getName().isPresent()) {
					throw new CompilationError(a,
							"All named parameters must be set after positional parameter. Parameter '"
									+ b.getName().get() + "' cannot be set at this position.");
				} else if (!b.getName().isPresent()) {
					return 1;
				}

				Integer apos = orderedParamNames.get(a.getName().get());

				if (apos == null) {
					throw new CompilationError(a,
							"Parameter '" + a.getName().get() + "' does not match any of the expected parameters");
				}

				Integer bpos = orderedParamNames.get(b.getName().get());

				if (bpos == null) {
					throw new CompilationError(a,
							"Parameter '" + b.getName().get() + "' does not match any of the expected parameters");
				}

				return apos.compareTo(bpos);
			});

			Set<String> names = new HashSet<>();
			Set<Integer> positons = new HashSet<>();
			int positon = 0;
			for (ArgumentListItemNode arg : argList) {
				if (arg.getName().isPresent()) {
					if (!names.add(arg.getName().get())) {
						throw new CompilationError(arg, "Duplicated named parameter '" + arg.getName().get()
								+ "'. Each parameter can only be set once.");
					} else if (positons.contains(orderedParamNames.get(arg.getName().get()))) {
						throw new CompilationError(arg, "Name parameter '" + arg.getName().get()
								+ "' refers to an already set positional parameter. Each parameter can only be set once.");
					}
				} else {
					positons.add(positon++);
				}
			}

			m.setArguments(ArgumentListNode.of(argList));
		}
	}

	private void addFreeTypes(List<CallableMemberMember<Method>> methodParameters,
			Map<String, List<ArgumentListItemNode>> freeArguments, TypeVariable paramType, ArgumentListItemNode arg) {

		if (paramType.getSymbol().isPresent()) {
			String symbol = paramType.getSymbol().get();
			List<ArgumentListItemNode> freeType = freeArguments.get(symbol);

			if (freeType == null) {
				freeType = new ArrayList<>(methodParameters.size());
				freeArguments.put(symbol, freeType);
			}

			freeType.add(arg);
		} else if (!paramType.getGenericParameters().isEmpty()) {
			for (TypeVariable g : paramType.getGenericParameters()) {
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

	private void promoteArithmeticOperatorToMethodCall(ExpressionNode parent, ExpressionNode leftExpression,
			ExpressionNode rightExpression, ArithmeticOperation operation) {

		TypeVariable left = this.getSemanticContext().ensureNotFundamental(leftExpression.getTypeVariable());
		TypeVariable right = this.getSemanticContext().ensureNotFundamental(rightExpression.getTypeVariable());

		if (left.equals(right) && left.getTypeDefinition().equals(LenseTypeSystem.String())) {
			// String concat
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

			parent.getParent().replace(parent, c);
		} else {
			// validate division by zero

			// find instance operator method
			TypeDefinition type = this.getSemanticContext().ensureNotFundamental(left.getTypeDefinition());

			if (type.equals(LenseTypeSystem.String())) {

				// String concat
				Optional<Method> method = typeAssistant.getMethodBySignature(type,new MethodSignature("asString"));

				checkAccess(type, method.get());
				
				MethodInvocationNode convert = new MethodInvocationNode(method.get(), rightExpression);
				convert.setTypeVariable(left);

				StringConcatenationNode concat = new StringConcatenationNode();
				concat.add(leftExpression);
				concat.add(convert);
				concat.setTypeVariable(left);

				parent.getParent().replace(parent, concat);
			} else if (operation == ArithmeticOperation.Division && leftExpression instanceof NumericValue
					&& typeAssistant.isAssignableTo(right, LenseTypeSystem.Rational()).matches()) {
				// natural / rational

				MethodSignature signature = new MethodSignature("invert");

				Optional<Method> method = typeAssistant.getMethodBySignature(right.getTypeDefinition(), signature);

				checkAccess(type, method.get());
				
				MethodInvocationNode invertRational = new MethodInvocationNode(method.get(), rightExpression);

				TypeVariable t = method.get().getReturningType();
				if (t == null) {
					throw new IllegalStateException("Type cannot be null");
				}
				invertRational.setTypeVariable(t);

				if (((NumericValue) leftExpression).isOne()) {
					parent.getParent().replace(parent, invertRational);

				} else {

					signature = new MethodSignature("multiply", new MethodParameter(right));

					method = typeAssistant.getMethodBySignature(right.getTypeDefinition(), signature);

					leftExpression.setTypeVariable(right);

					ArgumentListItemNode arg = new ArgumentListItemNode(0, leftExpression);
					arg.setExpectedType(right);

					checkAccess(type, method.get());
					
					MethodInvocationNode multiply = new MethodInvocationNode(method.get(), invertRational, arg);

					t = method.get().getReturningType();
					if (t == null) {
						throw new IllegalStateException("Type cannot be null");
					}
					multiply.setTypeVariable(t);
					parent.getParent().replace(parent, multiply);

				}

			} else if (operation == ArithmeticOperation.Division && rightExpression instanceof NumericValue
					&& ((NumericValue) rightExpression).isOne()) {

				parent.getParent().replace(parent, leftExpression);

			} else {
				MethodSignature signature = new MethodSignature(operation.equivalentMethod(),
						new MethodParameter(right, "text"));

				Optional<Method> method = typeAssistant.getMethodBySignature(type,signature);

				if (!method.isPresent()) {

					method = typeAssistant.getMethodByPromotableSignature(type,signature);

					if (!method.isPresent()) {

						// consider a wrap operation between non wrap numbers and wrap numbers

						if (operation == ArithmeticOperation.WrapMultiplication
								|| operation == ArithmeticOperation.WrapAddition
								|| operation == ArithmeticOperation.WrapSubtraction) {

							leftExpression = promote(parent, leftExpression, right, left);

							method = typeAssistant 
									.getMethodByPromotableSignature(leftExpression.getTypeVariable().getTypeDefinition(), signature);

						} else if (operation == ArithmeticOperation.Division) {

							rightExpression = promote(parent, rightExpression, left, right);

							method = typeAssistant 
									.getMethodByPromotableSignature(rightExpression.getTypeVariable().getTypeDefinition(), signature);

						}

						if (!method.isPresent()) {
							// search static operator
							throw new CompilationError(parent, "Method " + operation.equivalentMethod() + "(" + right
									+ ") is not defined in " + left);
						}

					} else {
						// Promote
						rightExpression = promote(parent, rightExpression, left, right);
					}
				}

				ArgumentListItemNode arg = new ArgumentListItemNode(0, rightExpression);
				arg.setExpectedType(rightExpression.getTypeVariable());

				MethodInvocationNode invokeOp = new MethodInvocationNode(method.get(), leftExpression, arg);

				List<CallableMemberMember<Method>> methodParameters = method.get().getParameters();
				if (methodParameters.size() != invokeOp.getCall().getArguments().getChildren().size()) {
					throw new CompilationError(parent, "Argument count does not match parameters count");
				}

				for (int i = 0; i < methodParameters.size(); i++) {
					MethodParameter param = (MethodParameter) methodParameters.get(i);
					ArgumentListItemNode a = (ArgumentListItemNode) invokeOp.getCall().getArguments().getChildren()
							.get(i);
					a.setExpectedType(param.getType());

				}

				parent.getParent().replace(parent, invokeOp);

				TypeVariable t = method.get().getReturningType();
				if (t == null) {
					throw new IllegalStateException("Type cannot be null");
				}
				invokeOp.setTypeVariable(t);

				parent.getParent().replace(parent, invokeOp);
			}
		}
	}

	private AstNode promoteNodeType(AstNode node, TypeVariable targetType) {

		TypeVariable nodeType = ((TypedNode) node).getTypeVariable();
		if (typeAssistant.isAssignableTo(nodeType, targetType).matches()) {
			return node;
		}

		if (typeAssistant.isMaybe(targetType)) {

			if (typeAssistant.isMaybe(nodeType)) {
				return node;
			}

			TypeVariable innerType = targetType.getGenericParameters().get(0);

			AstNode promoted = promoteNodeType(node, innerType);

			final TypeVariable typeofSome = getSemanticContext().resolveTypeForName("lense.core.lang.Some", 1).get();

			Constructor op = optionalConstructor(typeofSome, new ConstructorParameter(innerType));

			TypeVariable someTypeSpec = typeAssistant.specify(typeofSome, innerType);

			// read parent before adding to new node (parent will change)
			AstNode parent = node.getParent();

			NewInstanceCreationNode cn = NewInstanceCreationNode.of(someTypeSpec, op, promoted);
			cn.getCreationParameters().getTypeParametersListNode()
					.add(new GenericTypeParameterNode(new TypeNode(targetType)));

			parent.replace(node, cn);

			return cn;
		} else if (node instanceof NumericValue && typeAssistant.isNumber(targetType.getTypeDefinition())) {
			((NumericValue) node).setTypeVariable(targetType);
			return node;
		}

		if (!typeAssistant.isPromotableTo(nodeType, targetType)) {
			// TODO promote using extension
			throw new CompilationError(node, nodeType + " is not assignable to " + targetType);
		} else {
			Constructor op = optionalConstructor(targetType, new ConstructorParameter(nodeType));

			// read parent before adding to new node (parent will change)
			AstNode parent = node.getParent();

			NewInstanceCreationNode cn = NewInstanceCreationNode.of(op, node);
			cn.getCreationParameters().getTypeParametersListNode()
					.add(new GenericTypeParameterNode(new TypeNode(targetType)));

			parent.replace(node, cn);

			return cn;
		}

	}

	private Constructor optionalConstructor(TypeVariable typeVariable, ConstructorParameter... parameters) {

		List<Match<Constructor>> ops = typeAssistant.getConstructorByParameters(typeVariable.getTypeDefinition(), parameters);

		if (this.currentType != null && this.currentType.equals(typeVariable.getTypeDefinition())) {

			// try to find a more private constructor

			Optional<Match<Constructor>> p = ops.stream()
					.filter(c -> c.getCandidate().getVisibility() == Visibility.Private).findAny();
			if (p.isPresent()) {
				return p.get().getCandidate();
			}

			p = ops.stream().filter(c -> c.getCandidate().getVisibility() == Visibility.Protected).findAny();
			if (p.isPresent()) {
				return p.get().getCandidate();
			}
		}

		return ops.isEmpty() ? null : ops.get(0).getCandidate();
	}

	private ExpressionNode promote(LenseAstNode parent, ExpressionNode rightExpression, TypeVariable target,
			TypeVariable current) {

		if (typeAssistant.isAssignableTo(current,target).matches()) {
			return rightExpression;
		}

		Constructor op = optionalConstructor(target, new ConstructorParameter(current));

		if (op == null) {

			// if they are numbers other option exist
			throw new CompilationError(parent,
					"Implicit constructor not found to promote " + current + " to " + target);
		}

		if (rightExpression instanceof NumericValue) {
			NumericValue n = (NumericValue) rightExpression;

			n.setTypeVariable(target);

		} else {
			NewInstanceCreationNode cn = NewInstanceCreationNode.of(op, rightExpression);
			cn.getCreationParameters().getTypeParametersListNode()
					.add(new GenericTypeParameterNode(new TypeNode(target)));

			parent.replace(rightExpression, cn);

			return cn;
		}

		return rightExpression;
	}

	

	private Optional<Field> resolveFieldPropertyOrVariableName(AstNode node, FieldOrPropertyAccessNode m, TypeVariable currentType,
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
					m.setKind(property.get().getVisibility() == Visibility.Private ?  FieldOrPropertyAccessNode.FieldKind.FIELD  : FieldOrPropertyAccessNode.FieldKind.PROPERTY);
				}
			} else {
				m.setTypeVariable(variable.getTypeVariable());

				if (m.getAccessKind() == FieldAccessKind.READ) {
					m.getParent().replace(m, new VariableReadNode(name, variable));
				}
			}
		} else {

			m.setTypeVariable(field.get().getReturningType());
			m.setKind(FieldOrPropertyAccessNode.FieldKind.FIELD);
		}
		
		return field;
	}

	/**
	 * @param methodOwnerType
	 * @return
	 */
	private int countTupleSize(TypeDefinition methodOwnerType) {
		int count = 1;
		TypeVariable type = methodOwnerType.getGenericParameters().get(1).getUpperBound();
		while (!typeAssistant.isAssignableTo(type.getTypeDefinition(), LenseTypeSystem.Nothing()).matches()) {
			count++;

			type = type.getGenericParameters().get(1).getUpperBound();
		}
		return count;
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
		var list = argumentListNode.getChildren().stream().map(a -> ((ArgumentListItemNode) a).getFirstChild()).map(v -> {
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
			} else if (v instanceof CaptureReifiedTypesNode) {
				return null;
			} else {
				throw new RuntimeException();
			}
		}).filter(it -> it != null).collect(Collectors.toList());
				
		return list.toArray(new MethodParameter[list.size()]);
	}

}