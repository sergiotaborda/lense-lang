/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public class RangeTypeVariable implements IntervalTypeVariable {

	
	private String name;
	private Variance variance;
	private TypeVariable lower;
	private TypeVariable upper;
	
	public RangeTypeVariable (String name,Variance variance, TypeDefinition upper, TypeDefinition lower ){
		this(name, variance, new FixedTypeVariable(upper), new FixedTypeVariable(lower));
	}
	
	public RangeTypeVariable (String name,Variance variance, TypeVariable upper, TypeVariable lower ){
		this.name = name;
		this.variance = variance;
		this.upper = upper;
		this.lower = lower;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getLowerBound() {
		return lower;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getUpperbound() {
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
	public void setUpperBound(TypeVariable upper) {
		this.upper = upper;
	}


	@Override
	public IntervalTypeVariable changeBaseType(TypeDefinition concrete) {
		return this;
	}


	@Override
	public IntervalTypeVariable toIntervalTypeVariable() {
		return this;
	}

	@Override
	public List<IntervalTypeVariable> getGenericParameters() {
		return this.getLowerBound().getGenericParameters();
	}
	
	public boolean equals(Object other){
		if (this == other){
			return true;
		}
		if ( other instanceof RangeTypeVariable){
			RangeTypeVariable r = ((RangeTypeVariable)other);
			return r.variance.equals(this.variance) && r.getLowerBound().equals(this.lower) && r.getUpperbound().equals(this.upper);
		}
		return false;
	}
	
	public int hashCode(){
		return this.upper.hashCode();
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		return upper.getTypeDefinition();
	}

	@Override
	public boolean isSingleType() {
		 return this.upper.equals(this.lower);
	}

}
