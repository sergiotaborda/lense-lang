/**
 * 
 */
package lense.compiler.type;

import java.util.List;


/**
 * 
 */
public interface CallableMember<C extends CallableMember> extends TypeMember {

	public TypeDefinition getDeclaringType();

	public List<CallableMemberMember<C>> getParameters();
	
	public boolean isSynthetic();
}
