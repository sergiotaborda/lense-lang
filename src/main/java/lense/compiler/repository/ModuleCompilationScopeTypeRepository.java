package lense.compiler.repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lense.compiler.modules.ModuleDescription;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.modules.ModuleUnit;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class ModuleCompilationScopeTypeRepository implements UpdatableTypeRepository {

    private ModuleTypeContents currentModuleTypes;
    
    private List<ModuleUnit> requiredModules = new LinkedList<>();
    
    
    public ModuleCompilationScopeTypeRepository(ModuleDescription moduleDescriptor){
        this(new ModuleTypeContents(moduleDescriptor));
    }
    
    public ModuleCompilationScopeTypeRepository(ModuleTypeContents currentModuleTypes){
        this.currentModuleTypes = currentModuleTypes;
    }
    

    public void addRequiredModule(ModuleUnit moduleTypeContents) {
        requiredModules.add(moduleTypeContents);
    }
    
    @Override
    public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
        Optional<TypeDefinition> def = currentModuleTypes.resolveType(filter);
        if (def.isPresent()){
            return def;
        }
        
        for (var otherModule : requiredModules){
            def = otherModule.getTypeRepository().resolveType(filter);
            if (def.isPresent()){
                return def;
            }  
        }
        
        return Optional.empty();
    }

    @Override
    public TypeDefinition registerType(TypeDefinition type, int genericParametersCount) {
        return currentModuleTypes.registerType(type, genericParametersCount);
    }

    @Override
    public Map<Integer, TypeDefinition> resolveTypesMap(String name) {
        Map<Integer, TypeDefinition> map = currentModuleTypes.resolveTypesMap(name);
        if (map == null || map.isEmpty()){
            
            for (var otherModule : requiredModules){
                map = otherModule.getTypeRepository().resolveTypesMap(name);
                if (map != null && map.size() > 0){
                    return map;
                }  
            }
        }
        
        return map;
    }


}
