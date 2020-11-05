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
public class TestMathModuleCompilation {

	@Test  
	public void testMathModuleCompilationJava() {

		Assert.assertEquals(0, Lense.execute("compile --source=lense/math --repo=lense/sdk/compilation/modules".split(" ")));
	        
	}
	

}
