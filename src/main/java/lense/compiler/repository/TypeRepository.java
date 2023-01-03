/**
 * 
 */
package lense.compiler.repository;

import java.util.Map;
import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public interface TypeRepository {

	
	public static TypeRepository empty() {
		return new EmptyTypeRepository();
	}
	/**
	 * @param filter
	 * @return
	 */
	Optional<TypeDefinition> resolveType(TypeSearchParameters filter);

	Map<Integer, TypeDefinition> resolveTypesMap(String name);
	

}

