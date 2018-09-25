/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class MethodParameter implements MethodMember {

	private String name;
	private TypeVariable type;
	private Method declaringMethod;
	private boolean isMethodTypeBound;
	
	public MethodParameter(TypeVariable type) {
		this(type, "?");
	}
	
	public MethodParameter(TypeVariable type,String name) {
		if (type == null){
			throw new IllegalArgumentException("Type is required");
		}
		this.type = type;
		this.name = name;
	}
	
	public String toString(){
		return type.toString();
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
		return this == other || (  other instanceof MethodParameter && equals((MethodParameter)other));
	}

	public boolean equals(MethodParameter other){
		return this.type.equals(other.type);
	}

	public int hashCode(){
		return this.type.hashCode();
	}



	/**
	 * {@inheritDoc}
	 */
	@Override
	public Method getDeclaringMember() {
		return declaringMethod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclaringMember(Method method) {
		this.declaringMethod = method;
		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(method);
		}
	}
	
	@Override
	public CallableMemberMember<Method> attachTo(Method c) {
		MethodParameter p = new MethodParameter(type, name);
		p.declaringMethod = c;
		return p;
	}

	@Override
	public boolean typeEquals(CallableMemberMember<Method> other) {
		return other instanceof MethodParameter 
				&& this.type.equals(((MethodParameter)other).type);

	}

	public boolean isMethodTypeBound() {
		return isMethodTypeBound;
	}

	public void setMethodTypeBound(boolean isMethodTypeBound) {
		this.isMethodTypeBound = isMethodTypeBound;
	}


}
