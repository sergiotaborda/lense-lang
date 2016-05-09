package lense.compiler.visitor.copy;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;

public class AbstractLenseVisitor implements Visitor<AstNode>{

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
	}

}
