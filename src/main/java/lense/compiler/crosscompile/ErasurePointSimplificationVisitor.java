package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.type.variable.TypeVariable;

/**
 * Classifies cutting points as boxing or unboxing regardless of type
 * 
 */
public final class ErasurePointSimplificationVisitor implements Visitor<AstNode> {

	private TypeVariable expectedType;

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		if (node instanceof ErasurePointNode){

			AstNode parent = node.getParent();
			
			ErasurePointNode simpler = ((ErasurePointNode)node).simplify();

			parent.replace(node, simpler);

			return VisitorNext.Siblings;
		}
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {

	}



}
