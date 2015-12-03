/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.Variance;
/**
 * 
 */
public class VarianceNode extends JavaAstNode {

	private Variance variance;

	/**
	 * Constructor.
	 * @param variant
	 */
	public VarianceNode( Variance variance) {
		this.variance = variance;
	}

	public Variance getVariance() {
		return variance;
	}


}
