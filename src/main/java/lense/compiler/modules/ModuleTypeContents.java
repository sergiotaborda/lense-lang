/**
 * 
 */
package lense.compiler.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.repository.Version;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * Represents a TypeRepository for a given module
 */
public class ModuleTypeContents implements UpdatableTypeRepository {

	private String moduleName;
	private Version version;

	// type name -> type generics count -> type def
	private final Map<String,Map< Integer, TypeDefinition>> types = new HashMap<>();


	public ModuleTypeContents() {
		
	}

	public ModuleTypeContents(ModuleDescription descriptor) {
		this.moduleName =descriptor.getName();
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
		    return Collections.emptyMap();
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

		return Optional.empty();

	}

//	/**
//	 * @param name
//	 * @return
//	 */
//	private String resolveModule(String name) {
//		if (modules.isEmpty()){
//			return null;
//		}
//
//		QualifiedNameNode qn = new QualifiedNameNode(name).getPrevious();
//
//		while(qn != null){
//
//			if (modules.containsKey(qn.getName())){
//				return qn.getName();
//			}
//
//			qn= qn.getPrevious();
//		}
//
//		return null;
//	}

//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public List<ModularTypeRepository> resolveModuleByName(String qualifiedNameNode) {
//		if (moduleName.equals(qualifiedNameNode)){
//			return Collections.singletonList(this);
//		}
//
//
//		List<ModularTypeRepository> list = modules.get(qualifiedNameNode);
//
//		if (list == null){
//			return Collections.emptyList();
//		} else {
//			return list;
//		}
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public Optional<ModularTypeRepository> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
//		List<ModularTypeRepository> list = resolveModuleByName(identifier.getName());
//
//		return list.stream().filter(m -> m.getVersion().equals(version)).findAny();
//	}


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
			
			if (type.getGenericParameters().size() != genericParametersCount){
			    throw new IllegalArgumentException("Number of generic params does not match");
			}
			
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
			    // a type is already registered with another params count
			    cached = map.size() == 1 ? map.values().iterator().next() : null;
			    
			    if (cached != null){
			        if (cached != type){
			            cached.updateFrom(type);
			        } // else : they are the same. No need to do an update
			        
			        map.clear();
			        
			        if (type.getGenericParameters().size() != genericParametersCount){
		                throw new IllegalArgumentException("Number of generic params does not match");
		            }
			        
                    map.put(genericParametersCount, cached);
                    
                    return cached;
			    }
			    
				throw new IllegalStateException("Registered type does not match the new registed type");
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

			    if (LenseTypeSystem.getInstance().isAny(def)){
                    continue;
                }
			    
				LenseTypeDefinition type = (LenseTypeDefinition)def;

				
				Optional<TypeDefinition> op;
				if (type.getSuperDefinition() == null || LenseTypeSystem.getInstance().isAny(type.getSuperDefinition())){
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
			for(TypeDefinition matchInterface : type.getInterfaces()){
				
				Map<Integer, TypeDefinition> m = types.get(matchInterface.getName());
				
				if (m == null){
					throw new RuntimeException();
				} else if (m.size() == 1){
					LenseTypeDefinition realInterface = (LenseTypeDefinition)m.values().iterator().next();
					if (realInterface.isGeneric()){
						loadInterfaces(realInterface);
						newInterfaces.add(LenseTypeSystem.specify(realInterface, type.getGenericParameters()));
					} else {
						newInterfaces.add(realInterface);
					}
					
				} else {
					newInterfaces.add(resolveType(new TypeSearchParameters(matchInterface.getName(), matchInterface.getGenericParameters().size())).get());
				}
				
			}

			for (int i =0; i < newInterfaces.size(); i++){
				type.getInterfaces().set(i, newInterfaces.get(i));
			}
		}
	}



}
