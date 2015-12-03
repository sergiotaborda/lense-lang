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
public class TestSdkCompilation {

	
	@Test 
	public void testCompileLibrary() throws IOException {
		File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/sdk/");

		LenseCompiler compiler = new LenseCompiler();
	
		compiler.compileModuleFromDirectory(folder);
	}


}
