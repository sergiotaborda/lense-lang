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


}
