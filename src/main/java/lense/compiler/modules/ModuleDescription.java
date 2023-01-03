package lense.compiler.modules;

import java.util.Collection;
import java.util.Optional;

import lense.compiler.repository.Version;

public interface ModuleDescription {

	
	public default ModuleIdentifier getIdentifier() {
		return new ModuleIdentifier(this.getName(),this.getVersion());
	}
	
    public String getName();

    public Version getVersion();
    
    public Collection<ModuleDescription> getRequiredModules();

    public Optional<ModuleDescription> getRequiredModuleByName(String name);
    
    public default ModuleIdentifier getModuleIdentifier() {
        return new ModuleIdentifier(this.getName(), this.getVersion());
    }
}
