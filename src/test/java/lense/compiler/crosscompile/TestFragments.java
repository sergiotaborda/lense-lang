package lense.compiler.crosscompile;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.Test;

import compiler.CompilationError;
import compiler.filesystem.DiskSourceFileSystem;
import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.repository.ClasspathModulesRepository;

public class TestFragments {

	File base = new File(new File(".").getAbsoluteFile().getParentFile(), "lense/sdk/compilation/modules");
	
	ClasspathModulesRepository repo = new ClasspathModulesRepository(DiskSourceFileSystem.instance().folder(base));

    LenseCompiler compiler = new LenseToJavaCompiler(repo).setCompilerListener(new ThrowCompilationErrors());
	
    
	@Test  
	public void testMatrixCompilation() {
		
		compiler.compileUnit("""
				package test;
				
				import lense.core.collections.Array;
				
				public class Matrix<T> {

				  	private  constructor (public rowsCount : Natural, public columnsCount: Natural,  private cells : Array<T>);
				     
				     
				    public constructor ( rowsCount : Natural,  columnsCount: Natural,  seed : T){
				    	return new Matrix<T> (  rowsCount, columnsCount, new Array<T>(rowsCount * columnsCount, seed));
				    }
				    
				  
				    public [ row : Natural, column: Natural] : T {
				        get { 
				            return cells[calculateCell(row, column)];
				        }
				        set (value){
				           cells[calculateCell(row, column)] = value;
				        }
				    }
				     
				    private calculateCell( row : Natural,  column: Natural) : Natural{
				        return row * rowsCount + column;
				    }   
				}
				
			    public class Test  {

					public method (){
					
					  		let matrix = new Matrix<Integer>(3,3,0);

							assert(matrix[0,0] == 0, "matrix[0,0] is not zero");
					}
				}

				""");
		      
	}
	
	@Test  
	public void testBinaryArrayCompilation() {
		
		compiler.compileUnit("""
				package test;
				
				import lense.core.collections.Array;
				
			    public class Test  {

					public method (){
					
					  		let barray : Array<Boolean> = [false, true, true, false];

							let empty = barray.empty;
							
							barray[0] = empty;
							barray[0] = !!empty;
							barray[1] = !empty;
							
							assert(!barray.empty, "List size is empty");
									
					  		assert(!barray[0], "barray[0] is not false");
					  		assert(barray[1], "barray[1] is not true");
					  		assert(barray[2], "barray[2] is not true");
					  		assert(!barray[3], "barray[3] is not false");
					  		
					  		barray[0] = !!true;

					}
				}

				""");
		
//		  TODO no error when editing sequence
	        
	}
	
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
							
							mutable let sign : String = "+";
						    if (c < 0){
						    	sign = " - ";
						    }
							return c.asString() ++ sign ++  c.asString() ++ "i";
					}
				}

				""");

	        
	}
	
	@Test 
	public void testAssociationLiteral() {
		
		compiler.compileUnit("""
				package test;
				
				import lense.core.collections.Array;
				
			    public class Test  {

					public method (){
					
					  		let a = { "a" -> 1, "b" -> 2};
					}
				}

				""");
		       
	}

}
