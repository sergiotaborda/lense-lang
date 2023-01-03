package lense.compiler.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import compiler.filesystem.SourceFolder;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.modules.ModuleUnit;
import lense.compiler.modules.ModulesRepository;

public class ComposedModulesRepository implements ModulesRepository {

	private List<ModulesRepository> repositories = new LinkedList<>();
	
	public ComposedModulesRepository() {}
	
	public ComposedModulesRepository addRepositiory(ModulesRepository other) {
		
		repositories.add(other);
		
		return this;
	}

	@Override
	public Optional<ModuleUnit> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
		return repositories.stream().flatMap(r -> r.resolveModuleByNameAndVersion(identifier).stream()).findFirst();
	}

	@Override
	public List<SourceFolder> getClassPath() {
		return repositories.stream().flatMap(r -> r.getClassPath().stream()).collect(Collectors.toList());
	}
}
