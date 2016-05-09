/**
 * 
 */
package lense.compiler.typesystem;

public enum Variance {
	Invariant, // The type is fixed. Variant and Covariant positions have the same type of the upperbound
	ContraVariant, // in. Only values in the arguments list have the same type as upperbound.
	Covariant // out. Only values returning have the same type as upperbound
}