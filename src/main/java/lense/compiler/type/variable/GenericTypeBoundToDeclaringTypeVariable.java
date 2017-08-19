package lense.compiler.type.variable;

import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

public class GenericTypeBoundToDeclaringTypeVariable extends CalculatedTypeVariable {

	private TypeDefinition genericType;
	private TypeDefinition declaringType;
	private Variance variance;
	private String name;
	private int parameterIndex;

	public GenericTypeBoundToDeclaringTypeVariable(
			TypeDefinition parameterType,  // e.g. Maybe<T>
			TypeDefinition declaringType, // e.g. Interval <T>
			int index, // 0
			String name, // T 
			Variance variance) {
		this.genericType = parameterType;
		this.declaringType = declaringType;
		this.parameterIndex = index;
		this.name = name;
		this.variance = variance;
	}

	@Override
	public Variance getVariance() {
		return variance;
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return new GenericTypeBoundToDeclaringTypeVariable(genericType, concrete, parameterIndex, name, variance);
	}

	@Override
	protected IntervalTypeVariable original() {
		return new DeclaringTypeBoundedTypeVariable(declaringType,parameterIndex, name, variance);
	}
	
	public Optional<String> getSymbol() {
		return Optional.of(name);
	}
	
    @Override
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        this.declaringType = convert.apply(this.declaringType);
        this.genericType = convert.apply(this.genericType);
    }

}
