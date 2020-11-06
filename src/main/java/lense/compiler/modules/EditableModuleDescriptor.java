package lense.compiler.modules;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lense.compiler.repository.Version;

public class EditableModuleDescriptor implements ModuleDescription{

    private String name;
    private Version version;

    private List<ModuleDescription> requiredModules = new LinkedList<>();
    private Set<ModuleExport> exports = new HashSet<>();
    
    public EditableModuleDescriptor() {
    }
    
    public EditableModuleDescriptor(String name, Version version) {
        this.name = name;
        this.version = version;
    }

    @Override
    public String getName() {
       return name;
    }

    @Override
    public Version getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setVersion(Version version) {
        this.version = version;
    }

    public boolean addRequiredModule(EditableModuleDescriptor other) {
        if (this.requiredModules.stream().anyMatch(m -> m.getName().equals(other.name))){
           return false;
        }
        return this.requiredModules.add(other);
    }

    public boolean addExport(ModuleExport moduleExport) {
        return this.exports.add(moduleExport);
    }
    
    public Collection<ModuleExport> getExports(){
        return exports;
    }
    
    public Collection<ModuleDescription> getRequiredModules(){
        return requiredModules;
    }

    public Optional<ModuleDescription> getRequiredModuleByName(String name) {
         return this.requiredModules.stream().filter(m -> m.getName().equals(name)).findAny();
    }
    
    public boolean equals(Object other) {
    	return other instanceof EditableModuleDescriptor && equals((EditableModuleDescriptor)other);
    }
    
    public boolean equals(EditableModuleDescriptor other) {
    	return this.name.equals(other.name) && this.version.equals(other.version);
    }
    
    public int hashCode() {
    	return this.name.hashCode() + 31 * this.version.hashCode(); 
    }
}
