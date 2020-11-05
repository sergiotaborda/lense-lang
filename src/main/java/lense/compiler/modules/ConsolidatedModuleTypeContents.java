package lense.compiler.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class ConsolidatedModuleTypeContents extends ModuleTypeContents {

	private ModuleTypeContents mainContents;
	private List<ModuleTypeContents> dependencies = new LinkedList<>();


	public ConsolidatedModuleTypeContents(ModuleDescription descriptor,ModuleTypeContents main) {
		super(descriptor);
		mainContents = main;
	}


	public void addDependencyContent(ModuleTypeContents depcontent) {
		dependencies.add(depcontent);
	}
	
	public ModuleTypeContents simplify() {
		
		if (dependencies.isEmpty()) {
			return this.mainContents;
		}
		
		return this;
	}
	
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		
		var result = super.resolveType(filter);
		
		
		if(result.isPresent()) {
			return result;
		}
		
		result = this.mainContents.resolveType(filter);
		
		if(!result.isPresent()) {
			return result;
		}

		return Optional.of(result.get());
		
	}
	
	public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
		return super.resolveTypesMap(name);
	}
}
