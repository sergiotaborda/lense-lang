package lense.compiler.type.variable;

import lense.compiler.type.TypeMember;

public interface TypeMemberAwareTypeVariable extends TypeVariable {

	public void setDeclaringMember(TypeMember member);	
}
