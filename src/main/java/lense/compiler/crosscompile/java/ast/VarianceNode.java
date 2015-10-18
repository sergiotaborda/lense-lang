/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.syntax.AstNode;
import compiler.typesystem.Variance;

/**
 * 
 */
public class VarianceNode extends AstNode {

	private Variance variance;

	/**
	 * Constructor.
	 * @param variant
	 */
	public VarianceNode(Variance variance) {
		this.variance = variance;
	}

	public Variance getVariance() {
		return variance;
	}


}
