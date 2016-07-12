/**
 * 
 */
package lense.compiler.type.variable;

import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
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
	}
	
	protected IntervalTypeVariable original(){
		return declaringType.getGenericParameters().get(parameterIndex);
	}
	
	public String toString(){
		return getLowerBound().toString() + "<:" + getUpperbound().toString();
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
	public IntervalTypeVariable changeBaseType(TypeDefinition concrete) {
		return new DeclaringTypeBoundedTypeVariable(concrete, parameterIndex, symbol, positionVariance);
	}

	public int getIndex() {
		return parameterIndex;
	}


	


}
