/**
 * 
 */
package lense.compiler.type.variable;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public class InherintedTypeVariable extends CalculatedTypeVariable  {

	
	private TypeDefinition declaringClass;
	private int superTypeParameterIndex;
	private Variance positionVariance;

	public InherintedTypeVariable(TypeDefinition declaringClass, int superTypeParameterIndex,Variance positionVariance){
		this.declaringClass = declaringClass;
		this.superTypeParameterIndex = superTypeParameterIndex;
		this.positionVariance = positionVariance;
	}
	
	protected IntervalTypeVariable original(){
		return declaringClass.getSuperDefinition().getGenericParameters().get(superTypeParameterIndex);
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
		return new InherintedTypeVariable(concrete, superTypeParameterIndex,positionVariance );
	}




}
