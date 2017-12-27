package lense.compiler.type.variable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

/**
 * 
 *  Types of form A<T> where A is a type with generics T and T is a generic type of the declaring type
 *
 */
public class GenericTypeBoundToDeclaringTypeVariable extends CalculatedTypeVariable {

    private TypeDefinition genericType;
    private TypeDefinition declaringType;
    private Variance variance;
    private String name;
    private int parameterIndex;

    public GenericTypeBoundToDeclaringTypeVariable(
            TypeDefinition genericType,  // e.g. Maybe<T>
            TypeDefinition declaringType, // e.g. Interval <T>
            int index, // 0
            String name, // T 
            Variance variance) {
        this.genericType = genericType;
        this.declaringType = declaringType;
        this.parameterIndex = index;
        this.name = name;
        this.variance = variance;
    }
    

    @Override
    public boolean isSingleType() {
    	 return false;
    }
    

    public int getParameterIndex() {
    	return parameterIndex;
    }

    @Override
    public Variance getVariance() {
        return variance;
    }

    public String toString(){
        return genericType.getName() + "<" + super.toString() + ">";
    }

    @Override
    public List<TypeVariable> getGenericParameters() {
        return Collections.singletonList(this.original());
    }

    @Override
    public TypeVariable changeBaseType(TypeDefinition concrete) {
        return new GenericTypeBoundToDeclaringTypeVariable(genericType, concrete, parameterIndex, name, variance);
    }

    @Override
    protected TypeVariable original() {
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

	@Override
	public final TypeVariable getUpperBound() {
		TypeVariable bound;
		if (this.getVariance() == Variance.ContraVariant){
			bound =  original().getLowerBound();
		} else {
			bound = original().getUpperBound();
		}
		return new FixedTypeVariable(LenseTypeSystem.specify(genericType, bound));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final TypeVariable getLowerBound() {
		TypeVariable bound;
		if (this.getVariance() == Variance.ContraVariant){
			bound =  original().getUpperBound();
		} else {
			bound =  original().getLowerBound();
		}
		return new FixedTypeVariable(LenseTypeSystem.specify(genericType, bound));
	}
	
}
