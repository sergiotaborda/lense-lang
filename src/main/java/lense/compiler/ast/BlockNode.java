/**
 * 
 */
package lense.compiler.ast;

import java.util.Collection;
import java.util.LinkedList;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.VisitorNext;
import lense.compiler.context.SemanticContext;
import lense.compiler.phases.AbstractLenseVisitor;
import lense.compiler.phases.ScopeDelimiter;

/**
 * 
 */
public final class BlockNode extends LenseAstNode implements ScopeDelimiter{

	
	public BlockNode(){}

	@Override
	public String getScopeName() {
		return "block";
	}


	public Collection<ReturnNode> findAllReturnNodes(){
		
		ReturnVisitor visitor = new ReturnVisitor();
		TreeTransverser.transverse(this, visitor);
		return visitor.nodes;
	}
}

class ReturnVisitor extends AbstractLenseVisitor{

	public Collection<ReturnNode> nodes = new LinkedList<>();

	@Override
	protected SemanticContext getSemanticContext() {
		return null;
	}
	
	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof ReturnNode) {
			nodes.add((ReturnNode) node);
		}
	}


	
}