package lense.compiler.type.variable;

import java.util.List;

import lense.compiler.type.TypeDefinition;

public abstract class CalculatedTypeVariable implements TypeVariable{

	
	protected abstract TypeVariable original();
	
	@Override
	public final List<TypeVariable> getGenericParameters() {
		return original().getGenericParameters();
	}

	@Override
	public final TypeDefinition getTypeDefinition() {
		return original().getTypeDefinition();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getUpperBound() {
		return original().getUpperBound();
//		if (this.getVariance() == Variance.Covariant){
//			return original().getLowerBound();
//		} else {
//			return original().getUpperBound();
//		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getLowerBound() {
		return original().getLowerBound();
//		if (this.getVariance() == Variance.Covariant){
//			return original().getUpperBound();
//		} else {
//			return original().getLowerBound();
//		}
	}
	
	@Override
	public boolean isSingleType() {
		return getUpperBound().equals(getLowerBound());
	}
	
	@Override
	public boolean isFixed() {
		return false;
	}

	@Override
	public boolean isCalculated() {
		return true;
	}

	public String toString(){
		return getLowerBound().toString() + "<:" + getUpperBound().toString();
	}
}
