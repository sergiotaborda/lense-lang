package lense.compiler.crosscompile;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.Test;

import compiler.CompilationError;
import compiler.filesystem.DiskSourceFileSystem;
import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.repository.ClasspathModulesRepository;

public class TestNumericLiteralBounds {

	File base = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/sdk/compilation/modules");
	
	ClasspathModulesRepository repo = new ClasspathModulesRepository(DiskSourceFileSystem.instance().folder(base));

    LenseCompiler compiler = new LenseToJavaCompiler(repo).setCompilerListener(new ThrowCompilationErrors());
	
    
    @Test
    public void testMaxInteger() {
    	assertThrows(CompilationError.class, () -> 	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Int32;
			
				public class TestMaxInt32 {

    				private  value : Int32 = 2147483648;
				  	
				}
    	"""));
		      
    }
    
    @Test
    public void testMinInteger() {
    	assertThrows(CompilationError.class, () -> 	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Int32;
			
				public class TestMaxInt32 {

    				private  value : Int32 = -2147483649;
				  	
				}
    	"""));
		      
    }
    
    @Test
    public void testMaxLong() {
    	assertThrows(CompilationError.class, () -> 	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Int64;
			
				public class TestMaxInt64 {

    				private  value : Int64 = 9223372036854775809;
				  	
				}
    	"""));
		      
    }
    
    @Test
    public void testMinLong() {
    	assertThrows(CompilationError.class, () -> 	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Int64;
			
				public class TestMaxInt64 {

    				private  value : Int64 =  -9223372036854775809;
				  	
				}
    	"""));
		      
    }
    
    @Test
    public void testNoMaxWhole() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Integer;
			
				public class TestMaxInt64 {

    				private  value : Integer = 922337203685477580900;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testNoMinxWhole() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Integer;
			
				public class TestMaxInt64 {

    				private  value : Integer = -922337203685477580900;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testNoMaxRational() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Rational;
			
				public class TestMaxInt64 {

    				private  value : Rational = 922337203685477580900.00000000001;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testNoMinRational() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Rational;
			
				public class TestMaxInt64 {

    				private  value : Rational = -922337203685477580900.00000000001;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testHexaMaxValue() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Rational;
			
				public class TestMaxHexa {

    				private  value : Natural = #FFFFFFFF;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testBinaryNoMaxValue() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Rational;
			
				public class TestMaxBinary {

    				private  value : Integer = $11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111;
				  	
				}
    	""");
		      
    }
    
    @Test
    public void testParsingImaginaryNoBounds() {
     	compiler.compileUnit("""
				package test;
				
				import lense.core.math.Rational;
			
				public class TestMaxBinary {

    				private  value : Imaginary =  -922337203685477580900.00000000001i;
				  	
				}
    	""");
		      
    }
}
