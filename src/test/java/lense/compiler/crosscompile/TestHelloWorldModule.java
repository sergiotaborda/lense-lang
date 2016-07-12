/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import lense.compiler.LenseCompiler;
import lense.compiler.repository.ClasspathRepository;

/**
 * 
 */
public class TestHelloWorldModule {

	@Test  
	public void testHelloWorldCompilation() throws IOException {
		File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/helloworld");
	
		File base = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/sdk/compilation/modules");
		ClasspathRepository repo = new ClasspathRepository(base);

		final LenseCompiler compiler = new LenseCompiler(repo);
		compiler.compileModuleFromDirectory(moduleproject);
		
	}
}
