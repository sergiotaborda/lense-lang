/**
 * 
 */
package lense.compiler.typesystem;

import compiler.typesystem.GenericTypeParameter;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.Variance;

/**
 * 
 */
public class GenericDefinition implements GenericTypeParameter {

	
	private String name;
	private Variance variance;
	private TypeDefinition lower;
	private TypeDefinition upper;
	
	public GenericDefinition (String name,Variance variance, TypeDefinition upper, TypeDefinition lower ){
		this.name = name;
		this.variance = variance;
		this.upper = upper;
		this.lower = lower;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getLowerBound() {
		return lower;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getUpperbound() {
		return upper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Variance getVariance() {
		return variance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param self
	 */
	public void setUpperBound(TypeDefinition other) {
		this.upper = other;
	}

}
