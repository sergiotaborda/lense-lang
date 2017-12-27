/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Variance;

/**
 * 
 */
public class FixedTypeVariable implements TypeVariable {

	private TypeDefinition type;

	public FixedTypeVariable(TypeDefinition type){
		if (type == null){
			throw new IllegalArgumentException("Type is necessary");
		}
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getSymbol() {
		return Optional.empty();
	}
	
	public String toString(){
		return type.toString();
	}
	
	public TypeDefinition getTypeDefinition(){
		return type;
	}

	/**
	 * @return
	 */
	public boolean isFixed(){
		return true;
	}

	@Override
	public List<TypeVariable> getGenericParameters() {
		return type.getGenericParameters();
	}
	
	public boolean equals(Object other){
		return other instanceof FixedTypeVariable && ((FixedTypeVariable)other).type.equals(this.type);
	}
	
	public int hashCode(){
		return type.hashCode();
	}

	@Override
	public TypeVariable changeBaseType(TypeDefinition concrete) {
		return this;
	}

	@Override
	public boolean isSingleType() {
		return true;
	}

    @Override
    public void ensureNotFundamental(Function<TypeDefinition, TypeDefinition> convert) {
        this.type = convert.apply(type);
    }

	@Override
	public boolean isCalculated() {
		return false;
	}

	@Override
	public TypeVariable getLowerBound() {
		return this;
	}

	@Override
	public TypeVariable getUpperBound() {
		return this;
	}

	@Override
	public Variance getVariance() {
		return Variance.Invariant;
	}
}
