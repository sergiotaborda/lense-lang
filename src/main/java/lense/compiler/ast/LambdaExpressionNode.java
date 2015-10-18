/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import compiler.syntax.AstNode;

/**
 * 
 */
public class LambdaExpressionNode extends ExpressionNode {

	private ExpressionNode body;
	private AstNode parameters;
	
	/**
	 * @param astNode
	 */
	public void setBody(ExpressionNode body) {
		this.body = body;
		this.add(body);
	}

	/**
	 * @param astNode
	 */
	public void setParameters(AstNode parameters) {
		this.parameters = parameters;
		this.add(parameters);
	}
	
	public ExpressionNode getBody(){
		return body;
	}

	public AstNode getParameters(){
		return parameters;
	}
}
