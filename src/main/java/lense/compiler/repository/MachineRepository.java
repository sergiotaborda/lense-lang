/**
 * 
 */
package lense.compiler.repository;

import java.nio.file.Path;
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

    static String languageImplicitModule = "lense.core";
	private static ModuleRepository lenseModuleRepository;
	private Path classpath;
	
	public MachineRepository (){
		lenseModuleRepository = new LenseModuleRepository();
	}
	
	public MachineRepository (Path classpath){
		this.classpath = classpath;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		if (filter.getName().startsWith(languageImplicitModule)){ 
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
		super(MachineRepository.languageImplicitModule, new Version(0,0,1));
	}
	
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		return LenseTypeSystem.getInstance().getForName(filter.getName(), filter.getGenericParametersCount().orElse(0)).map(m -> (TypeDefinition)m);
	}
	
}
