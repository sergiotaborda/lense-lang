package lense.compiler.modules;

public class ModuleExport {

    private String name;
    private boolean doesIncludeAllSubLevels;

    public ModuleExport(String name, boolean doesIncludeAllSubLevels) {
        this.name = name;
        this.doesIncludeAllSubLevels = doesIncludeAllSubLevels;
    }
    
    @Override
    public boolean equals(Object other) {
        return other instanceof ModuleExport && ((ModuleExport)other).name.equals(name) && ((ModuleExport)other).doesIncludeAllSubLevels == doesIncludeAllSubLevels;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
