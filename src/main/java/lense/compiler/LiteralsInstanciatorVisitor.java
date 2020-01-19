/**
 * 
 */
package lense.compiler;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class LiteralsInstanciatorVisitor implements Visitor<AstNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitAfterChildren(AstNode node) {


		if (node instanceof ArithmeticNode){
			ArithmeticNode a = (ArithmeticNode) node;
			if (LenseTypeSystem.isAssignableTo(a.getTypeVariable(), LenseTypeSystem.String()).matches() ){
				StringConcatenationNode c;
				if (a.getLeft() instanceof StringConcatenationNode){
					c = (StringConcatenationNode)a.getLeft();
					c.add(a.getRight());
				} else {
					c= new StringConcatenationNode();
					c.add(a.getLeft());
					c.add(a.getRight());

				}
				a.getParent().replace(a, c);		

			}
		}

	}


}
