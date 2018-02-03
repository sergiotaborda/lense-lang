package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class EnsureNotFundamentalTypesVisitor extends AbstractScopedVisitor {

	public EnsureNotFundamentalTypesVisitor(SemanticContext context) {
		super(context);
	}


	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VisitorNext doVisitBeforeChildren(AstNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doVisitAfterChildren(AstNode node) {
		
		if (node instanceof TypeNode) {
			TypeNode tn = (TypeNode)node;
			
			tn.setTypeVariable(ensureNotFundamental(tn.getTypeVariable()));
		}
	}


	private TypeVariable ensureNotFundamental(TypeVariable type) {

		type.ensureNotFundamental(
				t -> getSemanticContext().resolveTypeForName(t.getName(), t.getGenericParameters().size()).get().getTypeDefinition());

		return type;

	}

}
