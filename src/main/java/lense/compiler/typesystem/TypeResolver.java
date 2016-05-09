/**
 * 
 */
package lense.compiler.typesystem;

import lense.compiler.type.TypeDefinition;

/**
 * 
 */
public interface TypeResolver {

	/**
	 * @param filter
	 * @return
	 */
	TypeDefinition resolveTypeByName(TypeSearchParameters filter);

	/**
	 * @param type
	 */
	void registerType(TypeDefinition type);


}
