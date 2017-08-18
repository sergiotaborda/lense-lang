/**
 * 
 */
package lense.compiler.crosscompile;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import lense.compiler.ast.LenseCompilerListener;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.SystemOutCompilerListener;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.crosscompile.javascript.LenseToJsCompiler;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.repository.TypeRepository;
import lense.compiler.repository.Version;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class TestSdkCompilation {


	@Test 
	public void testCompileLibrary() throws IOException {
		File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "/lense/sdk/");

		

		ModulesRepository repo = new ModulesRepository() {
            
            @Override
            public Optional<ModuleTypeContents> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
                return Optional.empty();
            }
            
            @Override
            public List<ModuleTypeContents> resolveModuleByName(String qualifiedNameNode) {
                return Collections.emptyList();
            }
        };
        new LenseToJavaCompiler(repo)
		//.setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
        .setCompilerListener(new SystemOutCompilerListener())
		.compileModuleFromDirectory(folder);
	}

	@Test 
    public void testCompileLibraryJavascript() throws IOException {
        File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "/lense/sdk/");

        ModulesRepository repo = new ModulesRepository() {
            
            @Override
            public Optional<ModuleTypeContents> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
                return Optional.empty();
            }
            
            @Override
            public List<ModuleTypeContents> resolveModuleByName(String qualifiedNameNode) {
                return Collections.emptyList();
            }
        };

        new LenseToJsCompiler(repo)
        .setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
        .compileModuleFromDirectory(folder);
    }

}
