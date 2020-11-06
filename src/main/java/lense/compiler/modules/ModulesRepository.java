package lense.compiler.modules;

import java.util.List;
import java.util.Optional;

import compiler.filesystem.SourceFolder;

/**
 * Represents a repository of modules.
 *
 */
public interface ModulesRepository {


    /**
     * @param qualifiedNameNode
     * @param version
     */
    public Optional<ModuleUnit> resolveModuleByNameAndVersion(ModuleIdentifier identifier);
    
    
    public List<SourceFolder> getClassPath();

}
