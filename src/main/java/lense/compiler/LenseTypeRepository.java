/**
 * 
 */
package lense.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.repository.ModuleRepository;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.repository.Version;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class LenseTypeRepository implements UpdatableTypeRepository{

	
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
		
		Optional<LenseTypeDefinition> result = instance.getForName(name, filter.getGenericParametersCount());

		if (result.isPresent()){
			mapping.put(filter, result.get());
			return Optional.of(result.get());
		}
		int pos = name.lastIndexOf('.');
		if (pos >0){
			name = name.substring(pos+1);
		}
		for(String packageName : instance.packageNames()){
			result = instance.getForName(packageName + "." + name, filter.getGenericParametersCount());

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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ModuleRepository> resolveModuleByName(QualifiedNameNode qualifiedNameNode) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<ModuleRepository> resolveModuleByNameAndVersion(QualifiedNameNode qualifiedNameNode, Version version) {
		throw new UnsupportedOperationException("Not implememented yet");
	}



}
