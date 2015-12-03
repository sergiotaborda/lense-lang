/**
 * 
 */
package lense.compiler.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.typesystem.LenseTypeSystem;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeSearchParameters;


/**
 * 
 */
public class MachineRepository implements TypeRepository {

	private static String languageImplicitModule = "lense.core";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		return  Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ModuleRepository> resolveModuleByName( QualifiedNameNode qualifiedNameNode) {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ModuleRepository> resolveModuleByNameAndVersion(	QualifiedNameNode qualifiedNameNode, Version version) {
		if (qualifiedNameNode.getName().equals(languageImplicitModule)){ 
			return Optional.of(new LenseModuleRepository());
		}
		return  Optional.empty();
	}

}

class LenseModuleRepository extends ModuleRepository{

	/**
	 * Constructor.
	 * @param module
	 * @param localRepository
	 */
	public LenseModuleRepository() {
		super("lense.lang", new Version(0,0,1));
	}
	
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		return LenseTypeSystem.getInstance().getForName(filter.getName(), filter.getGenericParametersCount()).map(m -> (TypeDefinition)m);
	}
	
}
