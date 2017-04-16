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

import lense.compiler.CompilationError;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.VersionNode;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class ModuleRepository implements UpdatableTypeRepository {

	private Map<String, List<ModuleRepository>> modules = new HashMap<String, List<ModuleRepository>>();
	private TypeRepository localRepository;
	private String moduleName;
	private Version version;

	// type name -> type generics count -> type def
	private Map<String,Map< Integer, TypeDefinition>> types = new HashMap<>();

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
	public ModuleRepository importModule(QualifiedNameNode qualifiedNameNode, VersionNode versionNode) {
		Optional<ModuleRepository> module = localRepository.resolveModuleByNameAndVersion(qualifiedNameNode,versionNode.getVersion()) ;
		if (!module.isPresent()){
			throw new CompilationError("Cannot import module " + qualifiedNameNode.getName() + " version " + versionNode.getVersion().toString() + ". Is it in the local repository?");
		}
		ArrayList<ModuleRepository> mods = new ArrayList<ModuleRepository>();

		mods.add(module.get());

		modules.put(module.get().getName(), mods );

		return module.get();
	}


	/**
	 * @return
	 */
	public String getName() {
		return moduleName;
	}
	
	@Override
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
		Map<Integer, TypeDefinition> map = types.get(name);
		
		if (map == null){
			
			if (localRepository != null){
				Optional<TypeDefinition> type = localRepository.resolveType(new TypeSearchParameters(name));
				
				if (type.isPresent()){
					return Collections.singletonMap(type.get().getGenericParameters().size(), type.get());
				}
				return Collections.emptyMap();
			}
		}
		
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {

		Map<Integer, TypeDefinition> map = types.get(filter.getName());

		if (map != null && map.size() == 1){
			if (filter.getGenericParametersCount().isPresent()){
				if (map.keySet().iterator().next().equals(filter.getGenericParametersCount().get())){
					return Optional.of(map.values().iterator().next());
				} 
			} else {
				return Optional.of(map.values().iterator().next());
			}
		}
		TypeDefinition it = map == null ? null : map.get(filter.getGenericParametersCount());

		if (it != null){
			return Optional.of(it);
		}

		if (localRepository != null){
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
	public Version getVersion() {
		return version;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {

		if (!types.containsKey(type.getName())){
			HashMap<Integer, TypeDefinition> map = new HashMap<>();
			map.put(genericParametersCount, type);
			types.put(type.getName(), map);
			return type;
		} else {

			Map<Integer, TypeDefinition> map = types.get(type.getName());

			TypeDefinition cached = map == null ? null : map.get(genericParametersCount);

			if (cached != null){
				cached.updateFrom(type);
				return cached;
			} else {
				throw new RuntimeException();
			}
			
		}

	}
	public void setVersion(Version version) {
		this.version = version;
	}

	public void setName(String moduleName ) {
		this.moduleName = moduleName;
	}

	public void simplify() {

		for(Map<Integer, TypeDefinition> map : types.values()){

			for(TypeDefinition def : map.values()){

				if (def.getName().equals(LenseTypeSystem.Any().getName())){
					continue;
				}
				LenseTypeDefinition type = (LenseTypeDefinition)def;

				Optional<TypeDefinition> op;
				if (LenseTypeSystem.Any().getName().equals(type.getSuperDefinition().getName())){
					op = Optional.of(LenseTypeSystem.Any());
				} else {
					Map<Integer, TypeDefinition> m = types.get(type.getSuperDefinition().getName());
					
					if (m.size() == 1){
						op = Optional.of(m.values().iterator().next());
					} else {
						op = resolveType(new TypeSearchParameters(type.getSuperDefinition().getName(), type.getSuperDefinition().getGenericParameters().size()));
					}

				}

				if (!op.isPresent()){
					throw new RuntimeException();
				}
				type.setSuperTypeDefinition(op.get());

				loadInterfaces(type);
			}
		}
	}
	private void loadInterfaces(LenseTypeDefinition type) {
		if (!type.getInterfaces().isEmpty()){
			List<TypeDefinition> newInterfaces = new ArrayList<>(type.getInterfaces().size());
			for(TypeDefinition it : type.getInterfaces()){
				
				Map<Integer, TypeDefinition> m = types.get(it.getName());
				
				if (m == null){
					throw new RuntimeException();
				} else if (m.size() == 1){
					LenseTypeDefinition t = (LenseTypeDefinition)m.values().iterator().next();
					if (t.isGeneric()){
						loadInterfaces(t);
						newInterfaces.add(LenseTypeSystem.specify(t, it.getGenericParameters().toArray(new lense.compiler.type.variable.TypeVariable[0])));
					} else {
						newInterfaces.add(t);
					}
					
				} else {
					newInterfaces.add(resolveType(new TypeSearchParameters(it.getName(), it.getGenericParameters().size())).get());
				}
				
			}

			for (int i =0; i < newInterfaces.size(); i++){
				type.getInterfaces().set(i, newInterfaces.get(i));
			}
		}
	}



}
