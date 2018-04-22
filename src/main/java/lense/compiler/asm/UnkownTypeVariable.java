package lense.compiler.asm;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Variance;

public class UnkownTypeVariable implements TypeVariable {

	public String toString() {
		return "Unkonwn";
	}
	
	private RuntimeException unsup() {
		return  new UnsupportedOperationException();
	}
	@Override
	public TypeVariable getLowerBound() {
		throw unsup();
	}

	@Override
	public TypeVariable getUpperBound() {
		throw unsup();
	}

	@Override
	public Variance getVariance() {
		throw unsup();
	}

	@Override
	public Optional<String> getSymbol() {
		throw unsup();
	}

	@Override
	public List<TypeVariable> getGenericParameters() {
		throw unsup();
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		throw unsup();
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		throw unsup();
	}

	@Override
	public boolean isSingleType() {
		throw unsup();
	}

	@Override
	public boolean isFixed() {
		throw unsup();
	}

	@Override
	public boolean isCalculated() {
		throw unsup();
	}

	@Override
	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {

		throw unsup();
	}

	@Override
	public boolean contains(TypeVariable thisType) {
		return false;
	}

}
