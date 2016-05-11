/**
 * 
 */
package lense.compiler.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;


/**
 * 
 */
public class MachineRepository implements TypeRepository {

	private static String languageImplicitModule = "lense.core";
	private static LenseModuleRepository lenseModuleRepository = new LenseModuleRepository();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		if (filter.getName().startsWith("lense.core")){ 
			return  lenseModuleRepository.resolveType(filter);
		}
		return  Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ModuleRepository> resolveModuleByName( QualifiedNameNode qualifiedNameNode) {
		if (qualifiedNameNode.getName().equals(languageImplicitModule)){ 
			return Collections.singletonList(lenseModuleRepository);
		}
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ModuleRepository> resolveModuleByNameAndVersion(	QualifiedNameNode qualifiedNameNode, Version version) {
		if (qualifiedNameNode.getName().equals(languageImplicitModule)){ 
			return Optional.of(lenseModuleRepository);
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