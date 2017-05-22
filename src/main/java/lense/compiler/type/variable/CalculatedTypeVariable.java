package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;

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
	public Optional<String> getSymbol() {
		return original().getSymbol();
	}

	@Override
	public boolean isSingleType() {
		return getLowerBound().equals(getUpperBound());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getUpperBound() {
		if (this.getVariance() == Variance.ContraVariant){
			return original().getLowerBound();
		} else {
			return original().getUpperBound();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getLowerBound() {
		if (this.getVariance() == Variance.ContraVariant){
			return original().getUpperBound();
		} else {
			return original().getLowerBound();
		}
	}
	
	@Override
	public boolean isFixed() {
		return false;
	}

	public String toString(){
		return getLowerBound().toString() + "<:" + getUpperBound().toString();
	}
}
