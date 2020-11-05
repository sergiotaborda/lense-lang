/**
 * 
 */
package lense.compiler.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lense.compiler.asm.UnkownTypeVariable;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.ContraVariantTypeVariable;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.MethodFreeTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
import lense.compiler.utils.Sequences;

/**
 * Represents a TypeRepository for a given module
 */
public class ModuleTypeContents implements UpdatableTypeRepository {


	// type name -> type generics count -> type def
	private final Map<String,Map< Integer, TypeDefinition>> types = new HashMap<>();
	private final ModuleDescription descriptor;

	public ModuleTypeContents(ModuleDescription descriptor) {
		this.descriptor = descriptor;
	}

	public ModuleDescription getDescription(){
		return descriptor;
	}
	
	
	@Override
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
		Map<Integer, TypeDefinition> map = types.get(name);
		if (map == null){
		    return java.util.Collections.emptyMap();
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
			var registeredCount = map.keySet().iterator().next();
			
			if (registeredCount != null && filter.getGenericParametersCount().isPresent() && filter.getGenericParametersCount().get().equals(registeredCount)){
				return Optional.of(map.values().iterator().next());
			} else {
				return Optional.of(map.values().iterator().next());
			}
		}
		TypeDefinition it = map == null ? null : map.get(filter.getGenericParametersCount().get());

		if (it != null){
			return Optional.of(it);
		}

		return Optional.empty();

	}

	private Optional<TypeDefinition> resolveType(TypeDefinition type) {
		return resolveType(new TypeSearchParameters(type.getName(), type.getGenericParameters().size()));
	}
	
	private Optional<TypeVariable> resolveType(TypeVariable type) {
		
		
		if(type.isSingleType()) {
			return resolveType(type.getTypeDefinition()).map(c -> (TypeVariable)c);
		}
		
	    if(type instanceof MethodFreeTypeVariable || type instanceof UnkownTypeVariable) {
	    	 return Optional.of(type);
		} else if(type instanceof RangeTypeVariable) {
			RangeTypeVariable range = (RangeTypeVariable)type;
			
			return Optional.of(new RangeTypeVariable(range.getSymbol(),range.getVariance(),resolveType(range.getUpperBound()).get() , resolveType(range.getLowerBound()).get()));
			
		} else if(type instanceof DeclaringTypeBoundedTypeVariable) {
			DeclaringTypeBoundedTypeVariable original = (DeclaringTypeBoundedTypeVariable)type;
			
			return Optional.of(new DeclaringTypeBoundedTypeVariable(resolveType(original.getDeclaringType()).get(), original.getParameterIndex(), original.getSymbol().get(),original.getVariance()));
			
		} else if(type instanceof GenericTypeBoundToDeclaringTypeVariable) {
			GenericTypeBoundToDeclaringTypeVariable original = (GenericTypeBoundToDeclaringTypeVariable)type;
			
			return Optional.of(new GenericTypeBoundToDeclaringTypeVariable(resolveType(original.getGenericType()).get(), resolveType(original.getDeclaringType()).get(),original.getParameterIndex(), original.getSymbol().get(),original.getVariance()));
			
		} else if(type instanceof ContraVariantTypeVariable) {
			ContraVariantTypeVariable original = (ContraVariantTypeVariable)type;
			
			return Optional.of(new ContraVariantTypeVariable(resolveType(original.getOriginal()).get()));
			
		}  else {
			
			throw new RuntimeException("Not supported consolidation for " + type.getClass().getName());
		}
		
	}
	
	
	public void removeType(String name) {
		 types.remove(name);
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
				cached.updateFrom(type, new LenseTypeAssistant(null));
				return cached;
			} else {
			    // a type is already registered with another params count
			    cached = map.size() == 1 ? map.values().iterator().next() : null;
			    
			    if (cached != null){
			        if (cached != type){
			        	
			        	if (type.isGeneric()) {
			        		
			        		if (isMoreGeneric(type, cached)) {
			        			cached  = type;
			        		} else {
			        			cached.updateFrom(type, new LenseTypeAssistant(null));
			        		}
			        	
			        		
			        	} else {
			        		cached.updateFrom(type, new LenseTypeAssistant(null));
			        	}
			        	
			            
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
	


	private boolean isMoreGeneric(TypeDefinition type, TypeDefinition cached) {
		
		if (type.isGeneric() && !cached.isGeneric()) {
			return true;
		}
		
		return Sequences.zipAny(type.getGenericParameters(), cached.getGenericParameters(), (a, b) -> {
			
			return !a.isSingleType() && b.isSingleType();
			
		});
	}


	public void consolidate() {

		for(Map<Integer, TypeDefinition> map : types.values()){

			for(TypeDefinition def : map.values()){

			    if (LenseTypeSystem.getInstance().isAny(def)){
                    continue;
                }
			    
				LenseTypeDefinition type = (LenseTypeDefinition)def;

				
				Optional<TypeDefinition> op;
				if (type.getSuperDefinition() == null || LenseTypeSystem.getInstance().isAny(type.getSuperDefinition())){
					
					Map<Integer, TypeDefinition> m = types.get(LenseTypeSystem.Any().getName());
					
					if (m == null) {
						op = Optional.of(LenseTypeSystem.Any());
					} else if (m.size() == 1){
						op = Optional.of(m.values().iterator().next());
					} else {
						op = resolveType(type.getSuperDefinition());
					}
					
				
				} else {
					Map<Integer, TypeDefinition> m = types.get(type.getSuperDefinition().getName());
					
					if (m.size() == 1){
						op = Optional.of(m.values().iterator().next());
					} else {
						op = resolveType(type.getSuperDefinition());
					}

				}

				if (!op.isPresent()){
					throw new RuntimeException();
				}
				type.setSuperTypeDefinition(op.get());

				loadInterfaces(type);
				
				loadConstructors(type.getConstructors().collect(Collectors.toList()));
			}
		}
	}
	

	private void loadConstructors(List<Constructor> all) {
		for(var c : all) {
			for (var p : c.getParameters()) {
				ConstructorParameter cp = (ConstructorParameter)p;
				
				var op = resolveType(cp.getType());
				
				if(op.isEmpty()) {
					op = resolveType(cp.getType());
					throw new RuntimeException("Cannot consolidate parameter " + cp.getType());
				}
				cp.setType(op.get());
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
						newInterfaces.add(LenseTypeSystem.getInstance().specify(realInterface, matchInterface.getGenericParameters()));
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
