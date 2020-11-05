package lense.compiler.phases;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.AbstractTypeResolverNode;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArgumentTypeResolverNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.CreationTypeNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.LiteralCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NewTypeResolverNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReceiveReifiedTypesNodes;
import lense.compiler.ast.ReificationScope;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeParameterTypeResolverNode;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableReadTypeResolverNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.TypeAssistant;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

public final class ReificationVisitor extends AbstractScopedVisitor {

	public static final String TYPE_REIFICATION_INFO = "$reificiationInfo$t";
	public static final String METHOD_REIFICATION_INFO = "$reificiationInfo$m";

	private LenseTypeDefinition currentType;
	private MethodDeclarationNode currentMethod;
	private TypeAssistant typeAssistant;
	
	public ReificationVisitor(SemanticContext semanticContext) {
		super(semanticContext);
		this.typeAssistant= new LenseTypeAssistant(semanticContext);
	}

	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		return Optional.of(currentType);
	}

	@Override
	public VisitorNext doVisitBeforeChildren(AstNode node) {

		if (node instanceof ClassTypeNode) {

			this.currentType = ((ClassTypeNode) node).getTypeDefinition();

		} else if (node instanceof ClassBodyNode) {
			
			if (!this.currentType.getKind().isInterface() &&  this.currentType.isGeneric()) {
				
				this.getSemanticContext().currentScope().defineVariable(
						TYPE_REIFICATION_INFO,
						LenseTypeSystem.ReifiedArguments(),
						node
				);
				
			}
		} else if (node instanceof MethodDeclarationNode) {
		
			currentMethod = (MethodDeclarationNode) node;
		}

		return VisitorNext.Children;
	}

	@Override
	public void doVisitAfterChildren(AstNode node) {

		if (node instanceof ConstructorDeclarationNode) {

			ConstructorDeclarationNode n = (ConstructorDeclarationNode) node;	

			// Add a invisible parameter for the receiver types
			List<TypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
			if (!genericParameters.isEmpty() && !(n.getParameters().getFirstChild() instanceof ReceiveReifiedTypesNodes)) {
				n.getParameters().addFirst(ReceiveReifiedTypesNodes.getInstance());
			}

		
		} else if (node instanceof NewInstanceCreationNode) {
			NewInstanceCreationNode n = (NewInstanceCreationNode) node;

			if (node instanceof LiteralCreation) {
				
				if (n.getCreationParameters().getTypeParametersListNode().getChildren().isEmpty()) {
					
					CreationTypeNode creationtype = new CreationTypeNode(n.getTypeNode().getName());
					
					for( AstNode p : n.getTypeNode().getChildren()) {
						creationtype.getTypeParametersListNode().add(p);
					}
					
					n.setCreationParameters(creationtype);
				}
				
			}
			
			if (!n.getTypeVariable().getGenericParameters().isEmpty()) {

				if (n.getArguments() != null && n.getArguments().getFirstChild() != null && n.getArguments().getFirstChild().getFirstChild() instanceof CaptureReifiedTypesNode) {
					return;
				}
				
				CaptureReifiedTypesNode capture = new CaptureReifiedTypesNode(ReificationScope.Type, n.getCreationParameters().getTypeParametersListNode());

				if (n.getCreationParameters().getTypeParametersListNode().getChildren().isEmpty()) {
					throw new RuntimeException("capture is empty");
				}
				
				int count = n.getCreationParameters().getTypeParametersListNode().getChildren().size();
				
				int position = 0;
				for (AstNode a : n.getCreationParameters().getTypeParametersListNode().getChildren()) {
					GenericTypeParameterNode g = (GenericTypeParameterNode) a;
					
					capture.add(resolveCapture(capture, a, position++, g.getTypeVariable(), node));
					
				}

				AstNode optimizedCapture = capture;
				
				if (count == 0) {
					// identity read
					VariableInfo variableInfo = this.getSemanticContext().currentScope()
							.searchVariable(TYPE_REIFICATION_INFO);

					VariableReadNode vr = new VariableReadNode(TYPE_REIFICATION_INFO, variableInfo);

					optimizedCapture = vr;
				}
				
				var captureArg = new ArgumentListItemNode(0, optimizedCapture);
				captureArg.setReificiationArgument(true);
				
				if (n.getArguments() == null) {
					n.setArguments(new ArgumentListNode(captureArg));
				} else {
					n.getArguments().addFirst(captureArg);
				}
			}

		} else if (node instanceof ClassTypeNode) {

			ClassTypeNode n = (ClassTypeNode) node;

			if (n.getKind() == LenseUnitKind.Class) {
				List<TypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
				if (!genericParameters.isEmpty()) {

					FieldDeclarationNode field = new FieldDeclarationNode(TYPE_REIFICATION_INFO, new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
					field.setInitializedOnConstructor(true);
					field.setVisibility(new VisibilityNode(Visibility.Private));
					field.setImutability(new ImutabilityNode(Imutability.Imutable));

					if(!n.getBody().getChildren().contains(field)) {
						n.getBody().add(field);
					}
		
				}
			}
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;
			
			if (!m.getMethodScopeGenerics().getChildren().isEmpty()) {
				m.getParameters().addFirst(new FormalParameterNode(METHOD_REIFICATION_INFO, LenseTypeSystem.ReifiedArguments()));
			}

		} else if (node instanceof MethodInvocationNode) {
			MethodInvocationNode m = (MethodInvocationNode)node;
			
			if (m.getBoundedTypes()!= null && !m.getBoundedTypes().isEmpty()) {
				VariableInfo info = this.getSemanticContext().currentScope().searchVariable(TYPE_REIFICATION_INFO);

				if (info == null) {
					// the type is not generic, only the method
					TypeParametersListNode params = new TypeParametersListNode();
					
					for (Map.Entry<String, TypeVariable> entry : m.getBoundedTypes().entrySet()) {
						TypeNode tt = new TypeNode(entry.getValue());

						
						params.add(new GenericTypeParameterNode(tt, Variance.Invariant));
					}
					
					CaptureReifiedTypesNode capture = new CaptureReifiedTypesNode(ReificationScope.Method, params);

					int position =0;
					for (AstNode a : params.getChildren()) {
						GenericTypeParameterNode g = (GenericTypeParameterNode) a;
						
						capture.add(resolveCapture(capture, a, position++, g.getTypeVariable(), node));
						
					}


					var arg = new ArgumentListItemNode(0, capture);
					arg.setReificiationArgument(true);
					
					arg.setExpectedType(LenseTypeSystem.ReifiedArguments());
					
					m.getCall().getArguments().addFirst(arg);
					
					
				} else {
					// the type is  generic
					throw new RuntimeException("Reification of method in generic class not implemented yet");
				}
			}
		}
	}

	private AstNode resolveCapture(CaptureReifiedTypesNode capture, AstNode a, int position, TypeVariable g, AstNode invocationNode) {
		if (g.getSymbol().isPresent()) {
		
			String symbol = g.getSymbol().get();
			Optional<Integer> index = currentType.getKind().isEnhancement() 
					?  ((LenseTypeDefinition)currentType.getSuperDefinition()).getGenericParameterIndexBySymbol(symbol) 
					:  currentType.getGenericParameterIndexBySymbol(symbol);
		
			Integer indexValue = null;
		
			if (index.isPresent()) {
				
				indexValue = index.get();

				AstNode tp = null;
				if (currentType.getKind().isEnhancement()) {
					
					tp =  new TypeParameterTypeResolverNode(new VariableReadTypeResolverNode(EnhancementVisitor.ENHANCED_OBJECT), indexValue);
				} else {
					VariableInfo variableInfo = this.getSemanticContext().currentScope()
							.searchVariable(TYPE_REIFICATION_INFO);

					VariableReadNode vr = new VariableReadNode(TYPE_REIFICATION_INFO, variableInfo);
				   
					MethodInvocationNode m = new MethodInvocationNode( vr, "typeAt",
							new ArgumentListNode(new NumericValue().setValue(new BigDecimal(indexValue),
									this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
											.get())));
					
					m.setTypeVariable(this.getSemanticContext().resolveTypeForName("lense.core.lang.reflection.TypeResolver", 0).get());
					
					tp = m;
				}
		
			   capture.count--;
			   return tp;
			} else {
				
				List<GenericTypeParameterNode> freeTypes = this.currentMethod.getMethodScopeGenerics().getChildren(GenericTypeParameterNode.class);
				
				for (int i = 0; i< freeTypes.size(); i++) {
					if ( freeTypes.get(i).getTypeNode().getName().equals(symbol)) {
						indexValue = i;
						break;
					}
				}

				if (indexValue == null) {
					
				
					if (invocationNode.getParent() instanceof ArgumentListItemNode) {
						ArgumentListItemNode arg = (ArgumentListItemNode)invocationNode.getParent();
					
				
							VariableInfo argVariable = this.getSemanticContext().currentScope()
									.searchVariable("");
							
							if (arg.isGeneric() && arg.getExpectedType().getSymbol().isPresent() && arg.getExpectedType().getSymbol().get().equals(g.getSymbol().get())) {
							
								return new ArgumentTypeResolverNode(arg);
								
							} else if(arg.getExpectedType() != null && typeAssistant.isAssignableTo(g, arg.getExpectedType()).matches() ) {
						
								return new ArgumentTypeResolverNode(arg);
						
							} else if (arg.getExpectedType() != null && !arg.getExpectedType().getGenericParameters().isEmpty()) {
					
								// TODO recursive
								int typeIndex = 0;
								for (TypeVariable gp : arg.getExpectedType().getGenericParameters()) {
									if (gp.getSymbol().isPresent()) {
										if (gp.getSymbol().get().equals(g.getSymbol().get())) {
											
											return new TypeParameterTypeResolverNode(new ArgumentTypeResolverNode(arg), typeIndex);
											
										}
									} else {
										if (typeIndex == position) {
											return new NewTypeResolverNode(gp);
										}
									}
									
									typeIndex++;
								}
							}
						
						
					} 

			
					if (invocationNode instanceof MethodInvocationNode) {
						MethodInvocationNode m = (MethodInvocationNode)invocationNode;
						
						Method mm = (Method)m.getTypeMember();
						
						// TODO recursive
						int typeIndex = 0;
						for ( CallableMemberMember<Method> cc : mm.getParameters()) {
							TypeVariable gp = cc.getType();
							Optional<AbstractTypeResolverNode> found = matchFreeType(position, g, m, typeIndex, gp);
							
							if (found.isPresent()) {
								return found.get();
							}
							position++;
						}
					}
					
					throw new CompilationError(invocationNode, "Generic type parameter " + symbol + "  is not defined");
					
				} else {
					VariableReadNode vr = new VariableReadNode(METHOD_REIFICATION_INFO, new VariableInfo(
							METHOD_REIFICATION_INFO, 
							this.getSemanticContext().resolveTypeForName("lense.core.lang.reflection.ReifiedArguments", 0).get(),
							this.currentMethod, 
							false, 
							true
						));
					
					MethodInvocationNode m = new MethodInvocationNode( vr, "typeAt",
							new ArgumentListNode(new NumericValue().setValue(new BigDecimal(indexValue),
									this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
											.get())));
					
					m.setTypeVariable(this.getSemanticContext().resolveTypeForName("lense.core.lang.reflection.TypeResolver", 0).get());
					
					 capture.count++;
					 
					 return m;
				}
				

			}
			

		

	
		} else if (g.isFixed() || g instanceof RangeTypeVariable) {
			
			if (g.getGenericParameters().isEmpty()) {
				return new NewTypeResolverNode(g);
			} else {
		
				AstNode[] params = new AstNode[g.getGenericParameters().size()];
				
				int index = 0;
				for (TypeVariable tv : g.getGenericParameters()) {
					params[index] = resolveCapture( capture,  a, index, tv, invocationNode);
					index++;
				}
				
				return new NewTypeResolverNode(g, params);
			}
			
		} else if (g instanceof GenericTypeBoundToDeclaringTypeVariable) {
			GenericTypeBoundToDeclaringTypeVariable gg = (GenericTypeBoundToDeclaringTypeVariable) g;

			VariableInfo variableInfo = this.getSemanticContext().currentScope()
					.searchVariable(TYPE_REIFICATION_INFO);

			VariableReadNode vr = new VariableReadNode(TYPE_REIFICATION_INFO, variableInfo);


			MethodInvocationNode m = new MethodInvocationNode(vr, "typeAt",
					new ArgumentListNode(new NumericValue().setValue(new BigDecimal(gg.getParameterIndex()),
							this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
									.get())));

			m.setTypeVariable(this.getSemanticContext()
					.resolveTypeForName("lense.core.lang.reflection.TypeResolver", 0).get());

			capture.count--;
			
			return m;
			
		} else if (g instanceof DeclaringTypeBoundedTypeVariable) {
			DeclaringTypeBoundedTypeVariable gg = (DeclaringTypeBoundedTypeVariable) g;
			
			VariableInfo variableInfo = this.getSemanticContext().currentScope()
					.searchVariable(TYPE_REIFICATION_INFO);

			VariableReadNode vr = new VariableReadNode(TYPE_REIFICATION_INFO, variableInfo);

			MethodInvocationNode m = new MethodInvocationNode(vr, "typeAt",
					new ArgumentListNode(new NumericValue().setValue(new BigDecimal(gg.getParameterIndex()),
							this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
									.get())));

			m.setTypeVariable(this.getSemanticContext()
					.resolveTypeForName("lense.core.lang.reflection.TypeResolver", 0).get());
			
			capture.count--;
			return m;
		} else {
			 // TODO 
			 throw new UnsupportedOperationException("Cannot capture reification");
		}

	}

	private Optional<AbstractTypeResolverNode> matchFreeType(int position, TypeVariable g, MethodInvocationNode m, int typeIndex, TypeVariable gp) {
		
		
		if (gp.getSymbol().isPresent()) {
			if (gp.getSymbol().get().equals(g.getSymbol().get())) {
				ArgumentListItemNode arg = m.getCall().getArguments().getChildren(ArgumentListItemNode.class).get(typeIndex);
				return Optional.of(new TypeParameterTypeResolverNode(new ArgumentTypeResolverNode(arg), typeIndex));
				
			}
		} else if (!gp.getGenericParameters().isEmpty()) {
			for ( TypeVariable t : gp.getGenericParameters()) {
				Optional<AbstractTypeResolverNode> found = matchFreeType(position, g, m, typeIndex, t);
				if (found.isPresent()) {
					return found;
				}
			}
			
		} else {
			if (typeIndex == position) {
				return Optional.of(new NewTypeResolverNode(gp));
			}
		}
		return Optional.empty();
	}

}
