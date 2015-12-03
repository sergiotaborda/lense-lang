/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.IOException;

import lense.compiler.LenseCompiler;

import org.junit.Test;

/**
 * 
 */
public class TestHelloWorldModule {

	@Test  
	public void testHelloWorldCompilation() throws IOException {
		File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/helloworld");
	
	
		final LenseCompiler compiler = new LenseCompiler();
		compiler.compileModuleFromDirectory(moduleproject);
		
	}
}
