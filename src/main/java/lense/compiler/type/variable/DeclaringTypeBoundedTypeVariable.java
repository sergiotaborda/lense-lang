/**
 * 
 */
package lense.compiler.type.variable;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public class DeclaringTypeBoundedTypeVariable extends CalculatedTypeVariable  {

	
	private TypeDefinition declaringType;
	private Variance positionVariance;
	private int parameterIndex;

	public DeclaringTypeBoundedTypeVariable (TypeDefinition declaringType, int parameterIndex, Variance positionVariance){
		this.positionVariance = positionVariance;
		this.declaringType = declaringType;
		this.parameterIndex= parameterIndex;
	}
	
	protected IntervalTypeVariable original(){
		return declaringType.getGenericParameters().get(parameterIndex);
	}
	
	public String toString(){
		return getLowerBound().toString() + "<:" + getUpperbound().toString();
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
		return new DeclaringTypeBoundedTypeVariable(concrete, parameterIndex, positionVariance);
	}

	


}
