/**
 * 
 */
package lense.compiler.ast;

import compiler.typesystem.Variance;

/**
 * 
 */
public class VarianceNode extends LenseAstNode {

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
