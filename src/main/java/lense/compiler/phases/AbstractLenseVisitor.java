package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.context.SemanticContext;

public  abstract class AbstractLenseVisitor implements Visitor<AstNode>{


	@Override
	public void startVisit() {
		// no-op
	}

	@Override
	public void endVisit() {
		// no-op
	}

    protected abstract SemanticContext getSemanticContext();
    
}
