/**
 * 
 */
package lense.compiler.typesystem;

public enum Variance {
	/**
	 * The type is fixed. Variant and Covariant positions have the same type of the upperbound
	 */
	Invariant, 
	/**
	 * in. Only values in the arguments list have the same type as upperbound.
	 */
	ContraVariant, 
	/**
	 *  out. Only values returning have the same type as upperbound
	 */
	Covariant 
}