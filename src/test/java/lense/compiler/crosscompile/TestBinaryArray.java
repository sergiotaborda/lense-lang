package lense.compiler.crosscompile;

import java.io.File;

import org.junit.Test;

import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.repository.ClasspathRepository;

public class TestBinaryArray {

	File base = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/sdk/compilation/modules");
	ClasspathRepository repo = new ClasspathRepository(base);

    LenseCompiler compiler = new LenseToJavaCompiler(repo);
	
	@Test  
	public void testBinaryArrayCompilation() {
		
		compiler.compileUnit("""
				package test;
				
				import lense.core.collections.Array;
				
			    public class Test  {

					public method (){
					
					  		let barray : Array<Boolean> = [false, true, true, false];

					  		assert(!barray[0], "barray[0] is not false");
					  		assert(barray[1], "barray[1] is not true");
					  		assert(barray[2], "barray[2] is not true");
					  		assert(!barray[3], "barray[3] is not false");
					  		
					  		barray[0] = true;

					}
				}

				""");
		
//		  TODO no error when editing sequence
	        
	}

}
