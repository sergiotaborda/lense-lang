package lense.compiler.modules;

import lense.compiler.repository.Version;

public interface ModuleDescription {

    public String getName();

    public Version getVersion();
    
    public default ModuleIdentifier getModuleIdentifier() {
        return new ModuleIdentifier(this.getName(), this.getVersion());
    }
}
