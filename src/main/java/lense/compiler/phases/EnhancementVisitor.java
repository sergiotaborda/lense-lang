package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeDefinition;

public final class EnhancementVisitor extends AbstractScopedVisitor {
	
	public static final String ENHANCED_OBJECT = "this$e";
	
	private LenseTypeDefinition currentType;

	public EnhancementVisitor(SemanticContext context) {
		super(context);
	}

	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		return Optional.of(currentType);
	}

	@Override
	protected VisitorNext doVisitBeforeChildren(AstNode node) {
		if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode) node;
			m.setStatic(true);

		
			m.getParameters().addFirst(new FormalParameterNode(ENHANCED_OBJECT, m.getMethod().getDeclaringType().getSuperDefinition()));
			
			transformReferenceToThis(m.getBlock());
			
			return VisitorNext.Siblings;
		}
		return VisitorNext.Children;
	}

	private void transformReferenceToThis(AstNode top) {
		if (top instanceof VariableReadNode) {
			VariableReadNode vr = (VariableReadNode)top;
			if (vr.getName().equals("this")) {
				vr.setName(ENHANCED_OBJECT);
			}

		} else {
			for (AstNode item : top.getChildren()) {
				transformReferenceToThis(item);
			}
		}
	}

	@Override
	protected void doVisitAfterChildren(AstNode node) {

	}

}
