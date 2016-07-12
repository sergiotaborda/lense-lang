/**
 * 
 */
package lense.compiler.type.variable;

import lense.compiler.typesystem.Variance;

/**
 * 
 */
public interface IntervalTypeVariable extends TypeVariable{

	/**
	 * @return
	 */
	TypeVariable getLowerBound();
	/**
	 * @return
	 */
	TypeVariable getUpperbound();
	
	/**
	 * @return
	 */
	Variance getVariance();




}
