/**
 * 
 */
package lense.compiler.type.variable;

import java.util.List;

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
	public String getName() {
		return type.getName();
	}
	
	public String toString(){
		return type.getName();
	}
	
	public TypeDefinition getTypeDefinition(){
		return type;
	}

	/**
	 * @return
	 */
	public boolean isConcrete(){
		return true;
	}

	@Override
	public IntervalTypeVariable toIntervalTypeVariable() {
		return new RangeTypeVariable(type.getName(), Variance.Invariant, type,type);
	}

	@Override
	public List<IntervalTypeVariable> getGenericParameters() {
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
}
