package lense.compiler.type;

import lense.compiler.type.variable.TypeVariable;

public interface CallableMemberMember<M extends CallableMember<?>> {

	public TypeVariable getType();
	public M getDeclaringMember();
	public void  setDeclaringMember(M method);
	public PositionalVariance getPositionVariance();
	public CallableMemberMember<M> attachTo(M parent);
	
	public boolean typeEquals(CallableMemberMember<M> other);


}
