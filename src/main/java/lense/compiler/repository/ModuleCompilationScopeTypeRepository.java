package lense.compiler.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.modules.ModuleDescription;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class ModuleCompilationScopeTypeRepository implements UpdatableTypeRepository {

    private ModuleTypeContents currentModuleTypes;
    
    private List<ModuleTypeContents> requiredModules = new LinkedList<>();
    
    
    public ModuleCompilationScopeTypeRepository(){
        this(new ModuleTypeContents(new ModuleDescription() {
            
            @Override
            public Version getVersion() {
                return null;
            }
            
            @Override
            public String getName() {
                return "currentModule";
            }
        }));
    }
    
    public ModuleCompilationScopeTypeRepository(ModuleTypeContents currentModuleTypes){
        this.currentModuleTypes = currentModuleTypes;
    }
    
    @Override
    public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
        Optional<TypeDefinition> def = currentModuleTypes.resolveType(filter);
        if (def.isPresent()){
            return def;
        }
        
        for (ModuleTypeContents otherModule : requiredModules){
            def = otherModule.resolveType(filter);
            if (def.isPresent()){
                return def;
            }  
        }
        
        return Optional.empty();
    }

    public void addRequiredModule(ModuleTypeContents moduleTypeContents) {
        requiredModules.add(moduleTypeContents);
    }

    @Override
    public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
        return currentModuleTypes.registerType(type, genericParametersCount);
    }

    @Override
    public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
        Map<Integer, TypeDefinition> map = currentModuleTypes.resolveTypesMap(name);
        if (map == null || map.isEmpty()){
            
            for (ModuleTypeContents otherModule : requiredModules){
                map = otherModule.resolveTypesMap(name);
                if (map != null && map.size() > 0){
                    return map;
                }  
            }
        }
        
        return map;
    }


}
