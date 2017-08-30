/**
 * 
 */
package lense.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;
/**
 * 
 */
public class FundamentalTypesModuleContents extends ModuleTypeContents {

	
	private Map<TypeSearchParameters, TypeDefinition> mapping = new HashMap<>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		TypeDefinition found = mapping.get(filter);
		
		if (found != null){
			return Optional.of(found);
		}
		String name = filter.getName();
		
		final LenseTypeSystem instance = LenseTypeSystem.getInstance();
		
		Optional<LenseTypeDefinition> result = instance.getForName(name, filter.getGenericParametersCount().orElse(0));

		if (result.isPresent()){
			mapping.put(filter, result.get());
			return Optional.of(result.get());
		}
		int pos = name.lastIndexOf('.');
		if (pos >0){
			name = name.substring(pos+1);
		}
		for(String packageName : instance.packageNames()){
			result = instance.getForName(packageName + "." + name, filter.getGenericParametersCount().orElse(0));

			if (result.isPresent()){
				mapping.put(filter, result.get());
				return Optional.of(result.get());
			}
		}
		
		return Optional.empty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
		TypeSearchParameters key = new TypeSearchParameters(type.getName(), genericParametersCount);
		if (!mapping.containsKey(key)){
			mapping.put(key, type);
			return type;
		} else {
			TypeDefinition original = mapping.get(key);
			original.updateFrom(type);
			return original;
		}
		
	}

	@Override
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
	    final LenseTypeSystem instance = LenseTypeSystem.getInstance();
        
       return instance.getAll().stream().filter(d -> d.getName().equals(name)).collect(Collectors.toMap(d -> d.getGenericParameters().size(), d-> d));

	}



}
