package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentTypeResolverNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableReadTypeResolverNode;
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
		} else if (node instanceof ClassTypeNode) {
			ClassTypeNode c  = (ClassTypeNode)node;
			if (c.getKind().isEnhancement()) {
				c.setSuperType(null);
			}
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
