package lense.compiler.type.variable;

import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public class UpdatableTypeVariable extends CalculatedTypeVariable {

	
	private TypeVariable original;

	public UpdatableTypeVariable(TypeVariable original) {
		this.original = original;
	}
	
	public void update(TypeVariable other) {
		this.original = other;
	}
	
	public TypeVariable original() {
		return this.original;
	}
	
	public Variance getVariance() {
		return original.getVariance();
	}

	public Optional<String> getSymbol() {
		return original.getSymbol();
	}

	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return original.changeBaseType(concrete);
	}

	public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
		original.ensureNotFundamental(convert);
	}




	
	
}
