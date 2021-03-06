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

import org.junit.Ignore;
import org.junit.Test;

import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFolder;
import lense.compiler.ast.LenseCompilerListener;
import lense.compiler.ast.SystemOutCompilerListener;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.crosscompile.javascript.LenseToJsCompiler;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleUnit;
import lense.compiler.modules.ModulesRepository;

/**
 * 
 */
public class TestSdkCompilation {

	SourceFolder sdkFolder = DiskSourceFileSystem.instance().folder(new File(new File(".").getAbsoluteFile().getParentFile(), "/lense/sdk/"));

	 @Test 
	public void testCompileLibrary() throws IOException {
		

	   
		ModulesRepository repo = new ModulesRepository() {
            
           
			@Override
			public List<SourceFolder> getClassPath() {
				return Collections.emptyList();
			}

			@Override
			public Optional<ModuleUnit> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
				return Optional.empty();
			}
        };
        new LenseToJavaCompiler(repo)
		//.setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
        .setCompilerListener(new SystemOutCompilerListener())
		.compileModuleFromDirectory(sdkFolder);
	}

	 @Ignore @Test 
    public void testCompileLibraryJavascript() throws IOException {
        
        ModulesRepository repo = new ModulesRepository() {
            
            @Override
            public Optional<ModuleUnit> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
                return Optional.empty();
            }
            
			
            @Override
			public List<SourceFolder> getClassPath() {
				// TODO Auto-generated method stub
				return null;
			}
        };

        new LenseToJsCompiler(repo)
        .setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
        .compileModuleFromDirectory(sdkFolder);
    }

}
