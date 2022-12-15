package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public class RecursiveTypeVariable implements TypeVariable {

	private LenseTypeDefinition reference;

	public RecursiveTypeVariable(LenseTypeDefinition reference) {
		this.reference = reference;
	}
	
	@Override
	public TypeVariable getLowerBound() {
		return reference;
	}

	@Override
	public TypeVariable getUpperBound() {
		return reference;
	}

	@Override
	public Variance getVariance() {
		return Variance.Invariant;
	}

	@Override
	public Optional<String> getSymbol() {
		return reference.getSymbol();
	}

	@Override
	public List<TypeVariable> getGenericParameters() {
		return reference.getGenericParameters();
	}

	@Override
	public TypeDefinition getTypeDefinition() {
		return reference;
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return new RecursiveTypeVariable((LenseTypeDefinition) reference.changeBaseType(concrete));
	}

	@Override
	public boolean isSingleType() {
		return true;
	}

	@Override
	public boolean isFixed() {
		return true;
	}

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		this.reference.ensureNotFundamental(convert);
	}

}
