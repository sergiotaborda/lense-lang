/**
 * 
 */
package lense.compiler.repository;

import lense.compiler.type.TypeDefinition;

/**
 * 
 */
public interface UpdatableTypeRepository extends TypeRepository {

   
    
	/**
	 * @param type
	 * @param genericParametersCount 
	 */
	TypeDefinition registerType(TypeDefinition type, int genericParametersCount);


}
