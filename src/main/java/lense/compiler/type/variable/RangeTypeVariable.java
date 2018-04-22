/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public class RangeTypeVariable implements TypeVariable {

	
	private Optional<String> symbol;
	private Variance variance;
	private TypeVariable lower;
	private TypeVariable upper;
	
	public RangeTypeVariable (String symbol,Variance variance, TypeDefinition upper, TypeDefinition lower ){
		this(Optional.of(symbol), variance, upper, lower);
	}
	
	public RangeTypeVariable (String symbol,Variance variance, TypeVariable upper, TypeVariable lower ){
		this(Optional.of(symbol), variance, upper, lower);
	}
	
	public RangeTypeVariable (Optional<String> symbol,Variance variance, TypeVariable upper, TypeVariable lower ){
		this.symbol = symbol;
		this.variance = variance;
		this.upper = upper;
		this.lower = lower;
	}
	
	public String toString(){
		if (symbol.isPresent()) {
			return lower + " < " + symbol.get() +  " < "  + upper; 
		} else {
			return lower + " < " +  upper; 
		}
		
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
	public TypeVariable getUpperBound() {
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
	public Optional<String> getSymbol() {
		return symbol;
	}

	/**
	 * @param self
	 */
	public void setUpperBound(TypeVariable upper) {
		this.upper = upper;
	}


	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return this;
	}


	@Override
	public List<TypeVariable> getGenericParameters() {
		return this.getLowerBound().getGenericParameters();
	}
	
	public boolean equals(Object other){
		if (this == other){
			return true;
		}
		if ( other instanceof RangeTypeVariable){
			RangeTypeVariable r = ((RangeTypeVariable)other);
			return (this.variance == this.variance || r.variance.equals(this.variance)) && r.getLowerBound().equals(this.lower) && r.getUpperBound().equals(this.upper);
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

	@Override
	public boolean isFixed() {
		return false;
	}
	
    @Override
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        this.upper.ensureNotFundamental(convert);
        this.lower.ensureNotFundamental(convert);
        
    }

	@Override
	public boolean isCalculated() {
		// TODO Auto-generated method stub
		return false;
	}



}
