/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.IOException;

import lense.compiler.LenseCompiler;
import lense.compiler.repository.MachineRepository;

import org.junit.Test;

/**
 * 
 */
public class TestHelloWorldModule {

	@Test  
	public void testHelloWorldCompilation() throws IOException {
		File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/helloworld");
	
		MachineRepository repo = new MachineRepository();

		final LenseCompiler compiler = new LenseCompiler(repo);
		compiler.compileModuleFromDirectory(moduleproject);
		
	}
}
