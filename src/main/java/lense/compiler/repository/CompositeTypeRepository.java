package lense.compiler.repository;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class CompositeTypeRepository implements TypeRepository {

	private List<TypeRepository> repos = new LinkedList<>();
	
	
	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		for(var other : repos) {
			var result = other.resolveType(filter);
			
			if(result.isPresent()) {
				return result;
			}
		}
		
		return Optional.empty();
	}

	
	public CompositeTypeRepository add(TypeRepository other) {
		repos.add(other);
		return this;
	}


	
	@Override
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
		for(var other : repos) {
			var result = other.resolveTypesMap(name);
			
			if(!result.isEmpty()) {
				return result;
			}
		}
		
		return Collections.emptyMap();
	}
}
