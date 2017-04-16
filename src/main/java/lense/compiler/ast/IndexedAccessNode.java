/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;


/**
 * 
 */
public class IndexedAccessNode extends NeedTypeCalculationNode{
	
	private ArgumentListNode arguments;
	private AstNode access;



	/**
	 * @param astNode
	 */
	public void setAccess(AstNode node) {
		this.access = node;
		this.add(node);
	}


	/**
	 * Obtains {@link AstNode}.
	 * @return the access
	 */
	public AstNode getAccess() {
		return access;
	}
	

	public ArgumentListNode getArguments() {
		return arguments;
	}


	public void setArguments(ArgumentListNode arguments) {
		this.arguments = arguments;
		this.add(arguments);
	}
}
