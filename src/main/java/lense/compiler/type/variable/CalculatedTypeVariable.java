package lense.compiler.type.variable;

import java.util.List;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public abstract class CalculatedTypeVariable implements IntervalTypeVariable{

	
	protected abstract IntervalTypeVariable original();
	
	@Override
	public IntervalTypeVariable toIntervalTypeVariable() {
		return this;
	}

	@Override
	public List<IntervalTypeVariable> getGenericParameters() {
		return original().getGenericParameters();
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		return original().getTypeDefinition();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return original().getName();
	}

	@Override
	public boolean isSingleType() {
		return getLowerBound().equals(getUpperbound());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getUpperbound() {
		if (this.getVariance() == Variance.ContraVariant){
			return original().getLowerBound();
		} else {
			return original().getUpperbound();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getLowerBound() {
		if (this.getVariance() == Variance.ContraVariant){
			return original().getUpperbound();
		} else {
			return original().getLowerBound();
		}
	}


}
