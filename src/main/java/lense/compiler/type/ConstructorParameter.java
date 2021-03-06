/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class ConstructorParameter implements CallableMemberMember<Constructor> {

	private String name;
	private TypeVariable type;
	private Constructor declaringMethod;
	
	
	public ConstructorParameter(TypeVariable type) {
		this(type, "<not supplied>");
	}
	
	public ConstructorParameter(TypeVariable type,String name) {
		if (type == null){
			throw new IllegalArgumentException("Type is required");
		}
		this.type = type;
		this.name = name;
	}
	
	public String toString(){
		return type.getTypeDefinition().getName()+ ":" + type.toString();
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

	@Override
	public CallableMemberMember<Constructor> attachTo(Constructor c) {
		ConstructorParameter p = new ConstructorParameter(type, name);
		p.declaringMethod = c;
		return p;
	}

	@Override
	public boolean typeEquals(CallableMemberMember<Constructor> other) {
		
		return other instanceof ConstructorParameter 
			&& this.type.equals(((ConstructorParameter)other).type);

	}





}
