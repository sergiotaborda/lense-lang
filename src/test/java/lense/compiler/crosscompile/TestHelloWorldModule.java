/**
 * 
 */
package lense.compiler.crosscompile;

import org.junit.Assert;
import org.junit.Test;

import lense.compiler.Lense;

/**
 * 
 */
public class TestHelloWorldModule {

	@Test  
	public void testHelloWorldCompilation() {
//		File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/helloworld");
//	
//		File base = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/sdk/compilation/modules");
//		ClasspathRepository repo = new ClasspathRepository(base);
//
//		final LenseCompiler compiler = new LenseCompiler(repo);
//		compiler.compileModuleFromDirectory(moduleproject);
		
		
	//	Lense.main("compile:java --source=lense/helloworld".split(" "));
		Assert.assertEquals(0, Lense.execute("compile --source=lense/helloworld --repo=lense/sdk/compilation/modules".split(" ")));
	        
	}
	
	@Test  
    public void testHelloWorldCompilationJavaScript() {

	    Assert.assertEquals(0, Lense.execute("compile:js --source=lense/helloworld".split(" ")));
 
    }
}
