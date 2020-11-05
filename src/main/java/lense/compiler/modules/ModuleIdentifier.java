package lense.compiler.modules;

import lense.compiler.repository.Version;

public final class ModuleIdentifier {

    private Version version;
    private String name;
    
    public ModuleIdentifier(String name, Version version) {
       this.name = name;
       this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object other) {
    	return other instanceof ModuleIdentifier && equals((ModuleIdentifier)other);
    }
    
    public boolean equals(ModuleIdentifier other) {
    	return this.name.equals(other.name) && this.version.equals(other.version);
    }
    
    public int hashCode() {
    	return this.name.hashCode() + 31 * this.version.hashCode();
    }
}
