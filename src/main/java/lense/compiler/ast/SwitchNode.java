/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.SwitchOptions;



/**
 * 
 */
public class SwitchNode extends LenseAstNode {

	private ExpressionNode candidate;
	private SwitchOptions switchOptions;

	/**
	 * @param expressionNode
	 */
	public void setCandidate(ExpressionNode exp) {
		this.candidate = exp;
		this.add(exp);
	}

	/**
	 * @param switchOptions
	 */
	public void setOptions(SwitchOptions switchOptions) {
		this.switchOptions = switchOptions;
		this.add(switchOptions);
	}

	/**
	 * Obtains {@link ExpressionNode}.
	 * @return the candidate
	 */
	public ExpressionNode getCandidate() {
		return candidate;
	}

	/**
	 * Obtains {@link SwitchOptions}.
	 * @return the switchOptions
	 */
	public SwitchOptions getOptions() {
		return switchOptions;
	}

}
