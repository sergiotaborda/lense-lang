package lense.compiler.crosscompile;

import java.io.File;

import org.junit.Test;

import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.repository.ClasspathRepository;

public class TestString {

	File base = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/sdk/compilation/modules");
	ClasspathRepository repo = new ClasspathRepository(base);

    LenseCompiler compiler = new LenseToJavaCompiler(repo);
	
	@Test  
	public void testStringInterpolation() {
		
		compiler.compileUnit("""
				package test;
				
				import lense.core.collections.Array;
				
			    public class Test  {

					public method (){
					
					  		let c = 2;
					  		
					  		assert(c == 2);
					  		assert(c == 2, "c is not 2");

							let s = "New value of {{ c }}";
							
							let u = "[" ++ c ++ "," ++ c ++ "," ++ c ++  "," ++ c ++ "]";
							
							var sign : String = "+";
						    if (c < 0){
						    	sign = " - ";
						    }
							return c.asString() ++ sign ++  c.asString() ++ "i";
					}
				}

				""");
		
//		  TODO no error when editing sequence
	        
	}

}
