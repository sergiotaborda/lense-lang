package lense.compiler.modules;

import lense.compiler.repository.JarTypeRepository;
import lense.compiler.repository.TypeRepositoryWithDependencies;

public class JarModule implements ModuleUnit {

	
	private final ModuleDescription moduleDescription;
	private final JarTypeRepository typeRepository;
	
	public JarModule(ModuleDescription moduleDescription, JarTypeRepository typeRepository) {
		this.moduleDescription = moduleDescription;
		this.typeRepository = typeRepository;
	}
	
	@Override
	public ModuleDescription getModuleDescription() {
		return moduleDescription;
	}

	@Override
	public TypeRepositoryWithDependencies getTypeRepository() {
		return typeRepository;
	}
	
	public String toString() {
		return moduleDescription.getName();
	}

}
