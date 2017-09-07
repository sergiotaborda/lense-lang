/**
 * 
 */
package lense.compiler.repository;

import java.util.Map;

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

	Map<Integer, TypeDefinition> resolveTypesMap(String name);
	



}
