package lense.compiler.modules;

import java.util.List;
import java.util.Optional;

/**
 * Rpresents a repository of modules.
 *
 */
public interface ModulesRepository {

    /**
     * @param qualifiedNameNode
     */
    List<ModuleTypeContents> resolveModuleByName(String qualifiedNameNode);

    /**
     * @param qualifiedNameNode
     * @param version
     */
    Optional<ModuleTypeContents> resolveModuleByNameAndVersion(ModuleIdentifier identifier);

}
