/**
 * 
 */
package lense.compiler.repository;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public interface UpdatableTypeRepository extends TypeRepository {

	/**
	 * @param type
	 */
	void registerType(TypeDefinition type);

}
