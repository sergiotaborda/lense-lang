/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class ConstructorParameter implements CallableMemberMember<Constructor> {

	private String name;
	private TypeVariable type;
	private Constructor declaringMethod;
	
	public ConstructorParameter(TypeDefinition type) {
		this(new FixedTypeVariable(type), "?");
	}
	
	public ConstructorParameter(TypeDefinition type, String name) {
		this(new FixedTypeVariable(type), name);
	}
	
	
	public ConstructorParameter(TypeVariable type) {
		this(type, "?");
	}
	
	public ConstructorParameter(TypeVariable type,String name) {
		if (type == null){
			throw new IllegalArgumentException("Type is required");
		}
		this.type = type;
		this.name = name;
	}
	
	public String toString(){
		return type.getName() + ":" + type.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	public String getName() {
		return name;
	}
	/**
	 * Atributes {@link string}.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * {@inheritDoc}
	 */
	
	public TypeVariable getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PositionalVariance getPositionVariance() {
		return PositionalVariance.In;
	}
	
	public void setType(TypeVariable type) {
		this.type = type;
	}

	
	
	public boolean equals(Object other){
		return this == other || (  other instanceof ConstructorParameter && equals((ConstructorParameter)other));
	}

	public boolean equals(ConstructorParameter other){
		return this.type.equals(other.type);
	}

	public int hashCode(){
		return this.type.hashCode();
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public Constructor getDeclaringMember() {
		return declaringMethod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclaringMember(Constructor method) {
		this.declaringMethod = method;
		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(method);
		}
	}
	
}
