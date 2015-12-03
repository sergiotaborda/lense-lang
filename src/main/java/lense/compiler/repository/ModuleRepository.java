/**
 * 
 */
package lense.compiler.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeSearchParameters;
import lense.compiler.CompilationError;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.VersionNode;

/**
 * 
 */
public class ModuleRepository implements UpdatableTypeRepository {

	private Map<String, List<ModuleRepository>> modules = new HashMap<String, List<ModuleRepository>>();
	private TypeRepository localRepository;
	private String moduleName;
	private Version version;
	
	private Map<TypeSearchParameters, TypeDefinition> types = new HashMap<TypeSearchParameters, TypeDefinition>();

	/**
	 * Constructor.
	 * @param module
	 * @param localRepository
	 */
	public ModuleRepository(ModuleNode module, TypeRepository localRepository) {
		this ( module.getName(), module.getVersion(),  localRepository);
	}
	public ModuleRepository(String moduleName, Version version) {
		this(moduleName, version, null);
	}
	
	public ModuleRepository(String moduleName, Version version, TypeRepository localRepository) {
		this.localRepository = localRepository;
		this.moduleName =moduleName;
		this.version =version;
	}

	/**
	 * @param qualifiedNameNode
	 * @param versionNode
	 */
	public void importModule(QualifiedNameNode qualifiedNameNode, VersionNode versionNode) {
		Optional<ModuleRepository> module = localRepository.resolveModuleByNameAndVersion(qualifiedNameNode,versionNode.getVersion()) ;
		if (!module.isPresent()){
			throw new CompilationError("Cannot import module " + qualifiedNameNode.getName() + " version " + versionNode.getVersion().toString() + ". Is it in the local repository?");
		}
		ArrayList<ModuleRepository> mods = new ArrayList<ModuleRepository>();
		
		mods.add(module.get());
		
		modules.put(module.get().getName(), mods );
	}
	

	/**
	 * @return
	 */
	private String getName() {
		return moduleName;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		
		TypeDefinition it = types.get(filter);
		
		if (it != null){
			return Optional.of(it);
		}
		
		
		Optional<TypeDefinition> type = localRepository.resolveType(filter);
		
		if (type.isPresent()){
			return type;
		}
		
		String moduleOfType = resolveModule(filter.getName());
		
		if (moduleOfType != null){
			for (ModuleRepository mr : modules.get(moduleOfType)){
				type = mr.resolveType(filter);
				if (type.isPresent()){
					return type;
				}
			}
		}
		
		return Optional.empty();
		
	}

	/**
	 * @param name
	 * @return
	 */
	private String resolveModule(String name) {
		if (modules.isEmpty()){
			return null;
		}
		
		QualifiedNameNode qn = new QualifiedNameNode(name).getPrevious();
		
		while(qn != null){
			
			if (modules.containsKey(qn.getName())){
				return qn.getName();
			}
			
			qn= qn.getPrevious();
		}
		
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ModuleRepository> resolveModuleByName(QualifiedNameNode qualifiedNameNode) {
		if (moduleName.equals(qualifiedNameNode)){
			return Collections.singletonList(this);
		}
		
		
		List<ModuleRepository> list = modules.get(qualifiedNameNode.getName());
		
		if (list == null){
			return Collections.emptyList();
		} else {
			return list;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ModuleRepository> resolveModuleByNameAndVersion( QualifiedNameNode qualifiedNameNode, Version version) {
		List<ModuleRepository> list = resolveModuleByName(qualifiedNameNode);

		return list.stream().filter(m -> m.getVersion().equals(version)).findAny();
	}


	/**
	 * @return
	 */
	private Version getVersion() {
		return version;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerType(TypeDefinition type) {
		types.put(new TypeSearchParameters(type.getName(), type.getGenericParameters().size()), type);
	}



}
