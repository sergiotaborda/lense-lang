package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public final class ContraVariantTypeVariable implements TypeVariable {

	private final TypeVariable original;


	public ContraVariantTypeVariable (TypeVariable original) {
		this.original = original;
	}
	
	public TypeVariable getLowerBound() {
		return original.getLowerBound();
	}

	public TypeVariable getUpperBound() {
		return original.getUpperBound();
	}

	public Variance getVariance() {
		return Variance.ContraVariant;
	}

	public Optional<String> getSymbol() {
		return original.getSymbol();
	}

	public List<TypeVariable> getGenericParameters() {
		return original.getGenericParameters();
	}

	public TypeDefinition getTypeDefinition() {
		return original.getTypeDefinition();
	}

	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return original.changeBaseType(concrete);
	}

	public boolean isSingleType() {
		return original.isSingleType();
	}

	public boolean isFixed() {
		return original.isFixed();
	}

	public boolean isCalculated() {
		return original.isCalculated();
	}

	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		original.ensureNotFundamental(convert);
	}

}
