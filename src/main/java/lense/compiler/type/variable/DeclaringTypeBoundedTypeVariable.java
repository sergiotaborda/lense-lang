/**
 * 
 */
package lense.compiler.type.variable;

import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * The type is the parameter type declared in the base class
 */
public class DeclaringTypeBoundedTypeVariable extends CalculatedTypeVariable  {

	
	private TypeDefinition declaringType;
	private Variance positionVariance;
	private int parameterIndex;
	private String symbol;

	public DeclaringTypeBoundedTypeVariable (TypeDefinition declaringType, int parameterIndex, String symbol, Variance positionVariance){
	    this.positionVariance = positionVariance;
		this.declaringType = declaringType;
		this.parameterIndex= parameterIndex;
		this.symbol = symbol;
		
		if (symbol.length() > 1) {
			throw new IllegalArgumentException("Possible illegal generic parameter name (" + symbol + ")");
		}
	}
	
	protected TypeVariable original(){
		return getDeclaringType().getGenericParameters().get(parameterIndex);
	}
	
	public Optional<String> getSymbol(){
		return Optional.of(symbol);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Variance getVariance() {
		return positionVariance;
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return new DeclaringTypeBoundedTypeVariable(concrete, parameterIndex, symbol, positionVariance);
	}

	public int getParameterIndex() {
		return parameterIndex;
	}

    @Override
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        this.declaringType = convert.apply(this.getDeclaringType());
    }


    public boolean equals(Object other){
		return other instanceof DeclaringTypeBoundedTypeVariable && equals((DeclaringTypeBoundedTypeVariable)other);
	}
	
    private boolean equals(DeclaringTypeBoundedTypeVariable other) {
    	return this.parameterIndex == other.parameterIndex && this.getDeclaringType().equals(other.getDeclaringType());
    }
    
	public int hashCode(){
		return parameterIndex ^ getDeclaringType().hashCode();
	}

	
	public TypeDefinition getDeclaringType() {
		return declaringType;
	}





}
