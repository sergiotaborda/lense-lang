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
		return getOriginal().getLowerBound();
	}

	public TypeVariable getUpperBound() {
		return getOriginal().getUpperBound();
	}

	public Variance getVariance() {
		return Variance.ContraVariant;
	}

	public Optional<String> getSymbol() {
		return getOriginal().getSymbol();
	}

	public List<TypeVariable> getGenericParameters() {
		return getOriginal().getGenericParameters();
	}

	public TypeDefinition getTypeDefinition() {
		return getOriginal().getTypeDefinition();
	}

	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return getOriginal().changeBaseType(concrete);
	}

	public boolean isSingleType() {
		return getOriginal().isSingleType();
	}

	public boolean isFixed() {
		return getOriginal().isFixed();
	}

	public boolean isCalculated() {
		return getOriginal().isCalculated();
	}

	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		getOriginal().ensureNotFundamental(convert);
	}

	
	public TypeVariable getOriginal() {
		return original;
	}

}
