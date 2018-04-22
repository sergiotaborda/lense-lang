/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class MethodReturn implements MethodMember {

	private Method method;
	private TypeVariable type;
	
	public MethodReturn(TypeVariable type){
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Method getDeclaringMember() {
		return method;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDeclaringMember(Method method) {
		this.method = method;
		if (type instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)type).setDeclaringMember(method);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PositionalVariance getPositionVariance() {
		return PositionalVariance.Out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeVariable getType() {
		return type;
	}

	@Override
	public CallableMemberMember<Method> attachTo(Method c) {
		MethodReturn p = new MethodReturn(type);
		p.method = c;
		return p;
	}

	@Override
	public boolean typeEquals(CallableMemberMember<Method> other) {
		return other instanceof MethodReturn 
				&& this.type.equals(((MethodReturn)other).type);

	}
}
