package lense.compiler.phases;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.CreationTypeNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.LiteralCreation;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReceiveReifiedTypesNodes;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Visibility;

public class ReificationVisitor extends AbstractScopedVisitor {

	public static final String REIFICATION_INFO = "$reificiationInfo";

	private LenseTypeDefinition currentType;

	public ReificationVisitor(SemanticContext semanticContext) {
		super(semanticContext);
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
			this.getSemanticContext().currentScope().defineVariable(
					REIFICATION_INFO,
					this.getSemanticContext().resolveTypeForName("lense.core.lang.reflection.ReifiedArguments", 0).get(),
					node
			);
		}

		return VisitorNext.Children;
	}

	@Override
	public void doVisitAfterChildren(AstNode node) {

		if (node instanceof ConstructorDeclarationNode) {

			ConstructorDeclarationNode n = (ConstructorDeclarationNode) node;

			List<TypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
			if (!genericParameters.isEmpty()) {
				n.getParameters().addFirst(new ReceiveReifiedTypesNodes());
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
				
				AstNode capture = new CaptureReifiedTypesNode(n.getCreationParameters().getTypeParametersListNode());

				if (n.getCreationParameters().getTypeParametersListNode().getChildren().isEmpty()) {
					throw new RuntimeException("capture is empty");
				}
				
				for (AstNode a : n.getCreationParameters().getTypeParametersListNode().getChildren()) {
					GenericTypeParameterNode g = (GenericTypeParameterNode) a;
					if (g.getTypeVariable().getSymbol().isPresent()) {
					
						Optional<Integer> index = currentType.getGenericParameterIndexBySymbol(g.getTypeVariable().getSymbol().get());
						
						VariableInfo variableInfo = this.getSemanticContext().currentScope()
								.searchVariable(REIFICATION_INFO);

						VariableReadNode vr = new VariableReadNode(REIFICATION_INFO, variableInfo);

						MethodInvocationNode m = new MethodInvocationNode(vr, "fromIndex",
								new ArgumentListNode(new NumericValue().setValue(new BigDecimal(index.get()),
										this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
												.get())));
						
						m.setTypeVariable(this.getSemanticContext().resolveTypeForName("lense.core.lang.reflection.ReifiedArguments", 0).get());

						capture = m;
						
					} else if (g.getTypeVariable().isFixed() || g.getTypeVariable() instanceof RangeTypeVariable) {
						continue;
					} else if (g.getTypeVariable() instanceof GenericTypeBoundToDeclaringTypeVariable) {
						GenericTypeBoundToDeclaringTypeVariable gg = (GenericTypeBoundToDeclaringTypeVariable) g.getTypeVariable();

						VariableInfo variableInfo = this.getSemanticContext().currentScope()
								.searchVariable(REIFICATION_INFO);

						VariableReadNode vr = new VariableReadNode(REIFICATION_INFO, variableInfo);

						MethodInvocationNode m = new MethodInvocationNode(vr, "fromIndex",
								new ArgumentListNode(new NumericValue().setValue(new BigDecimal(gg.getParameterIndex()),
										this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
												.get())));

						m.setTypeVariable(this.getSemanticContext()
								.resolveTypeForName("lense.core.lang.reflection.ReifiedArguments", 0).get());

						capture = m;
					} else if (g.getTypeVariable() instanceof DeclaringTypeBoundedTypeVariable) {
						DeclaringTypeBoundedTypeVariable gg = (DeclaringTypeBoundedTypeVariable) g.getTypeVariable();
						
						VariableInfo variableInfo = this.getSemanticContext().currentScope()
								.searchVariable(REIFICATION_INFO);

						VariableReadNode vr = new VariableReadNode(REIFICATION_INFO, variableInfo);

						MethodInvocationNode m = new MethodInvocationNode(vr, "fromIndex",
								new ArgumentListNode(new NumericValue().setValue(new BigDecimal(gg.getParameterIndex()),
										this.getSemanticContext().resolveTypeForName("lense.core.math.Natural", 0)
												.get())));

						m.setTypeVariable(this.getSemanticContext()
								.resolveTypeForName("lense.core.lang.reflection.ReifiedArguments", 0).get());
						
					} else {
						 // TODO 
						 throw new UnsupportedOperationException("Cannot capture reification");
					}
				}

				
				
				if (n.getArguments() == null) {
					n.setArguments(new ArgumentListNode(new ArgumentListItemNode(0, capture)));
				} else {
					n.getArguments().addFirst(new ArgumentListItemNode(0, capture));
				}
			}

		} else if (node instanceof ClassTypeNode) {

			ClassTypeNode n = (ClassTypeNode) node;

			if (n.getKind() == LenseUnitKind.Class) {
				List<TypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
				if (!genericParameters.isEmpty()) {

					FieldDeclarationNode field = new FieldDeclarationNode(REIFICATION_INFO,
							new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
					field.setInitializedOnConstructor(true);
					field.setVisibility(new VisibilityNode(Visibility.Private));
					field.setImutability(new ImutabilityNode(Imutability.Imutable));

					n.getBody().add(field);
				}
			}

		}
	}

}
