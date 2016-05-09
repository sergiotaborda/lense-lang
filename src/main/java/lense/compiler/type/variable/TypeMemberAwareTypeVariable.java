package lense.compiler.type.variable;

import lense.compiler.type.TypeMember;

public interface TypeMemberAwareTypeVariable extends IntervalTypeVariable {

	public void setDeclaringMember(TypeMember member);	
}
