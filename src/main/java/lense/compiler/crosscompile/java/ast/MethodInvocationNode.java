/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.syntax.AstNode;

/**
 * 
 */
public class MethodInvocationNode extends NeedTypeCalculationNode {

	private MethodCallNode call;
	private AstNode access;

	/**
	 * @param methodCallNode
	 */
	public void setCall(MethodCallNode call) {
		this.call = call;
		this.add(call);
	}

	/**
	 * @param astNode
	 */
	public void setAccess(AstNode node) {
		this.access = node;
		this.add(node);
	}

	/**
	 * Obtains {@link MethodCallNode}.
	 * @return the call
	 */
	public MethodCallNode getCall() {
		return call;
	}

	/**
	 * Obtains {@link AstNode}.
	 * @return the access
	 */
	public AstNode getAccess() {
		return access;
	}
	
	

}
