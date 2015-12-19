/**
 * 
 */
package lense.compiler.repository;

import java.util.List;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public interface TypeRepository {

	/**
	 * @param filter
	 * @return
	 */
	Optional<TypeDefinition> resolveType(TypeSearchParameters filter);

	/**
	 * @param qualifiedNameNode
	 */
	List<ModuleRepository> resolveModuleByName(QualifiedNameNode qualifiedNameNode);

	/**
	 * @param qualifiedNameNode
	 * @param version
	 */
	Optional<ModuleRepository> resolveModuleByNameAndVersion(QualifiedNameNode qualifiedNameNode, Version version);


}