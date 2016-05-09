/**
 * 
 */
package lense.compiler.type;

import java.util.List;


/**
 * 
 */
public interface CallableMember extends TypeMember {

	public TypeDefinition getDeclaringType();

	//public int getModifiers();
	public List<MethodParameter> getParameters();
	
	public boolean isSynthetic();
}
