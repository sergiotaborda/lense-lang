package lense.compiler.modules;

import lense.compiler.repository.TypeRepositoryWithDependencies;

public interface ModuleUnit {

	public ModuleDescription getModuleDescription();
	public TypeRepositoryWithDependencies getTypeRepository();
}
