/**
 * 
 */
package lense.compiler.repository;

import java.util.List;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public interface TypeRepository {

	/**
	 * @param filter
	 * @return
	 */
	Optional<TypeDefinition> resolveType(TypeSearchParameters filter);


	

}
