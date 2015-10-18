/**
 * 
 */
package lense.compiler.crosscompile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import lense.compiler.LenseCompiler;
import lense.compiler.LenseGrammar;
import lense.compiler.typesystem.LenseTypeSystem;

import org.junit.Ignore;
import org.junit.Test;

import compiler.CompilationUnitSet;
import compiler.Compiler;
import compiler.FileCompilationUnit;
import compiler.FirstFollowTable;
import compiler.FirstFollowTableCalculator;
import compiler.FolderCompilationUnionSet;
import compiler.PrintOutBackEnd;
import compiler.PromisseSet;
import compiler.RealizedPromisseSet;
import compiler.lexer.ListCompilationUnitSet;
import compiler.parser.Identifier;
import compiler.parser.LRZeroAutomatonFactory;
import compiler.parser.LookupTable;
import compiler.parser.LookupTableAction;
import compiler.parser.LookupTableRow;
import compiler.parser.MatchableProduction;
import compiler.parser.NonTerminal;
import compiler.parser.Numeric;
import compiler.parser.Production;
import compiler.parser.SLRAutomatonFactory;
import compiler.parser.SplitAction;
import compiler.parser.Terminal;
import compiler.parser.Text;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class TestLenseGrammar {

	

	@Test @Ignore
	public void testFirstAndFollow()  {
	
		LenseGrammar g = new LenseGrammar();
		
		FirstFollowTable firstFollowTable = new FirstFollowTableCalculator().calculateFrom(g.getStartProduction());
		
		assertFalse(firstFollowTable.followOf(new NonTerminal("packageDeclaration")).isEmpty());
		assertFalse(firstFollowTable.followOf(new NonTerminal("superDeclaration")).isEmpty());
		assertFalse(firstFollowTable.followOf(new NonTerminal("blockStatement")).isEmpty());
		
		assertFalse(firstFollowTable.firstOf(new NonTerminal("typeDeclaration")).isEmpty());
		assertFalse(firstFollowTable.firstOf(new NonTerminal("type")).isEmpty());
		assertFalse(firstFollowTable.firstOf(new NonTerminal("localVariableDeclaration")).isEmpty());
		assertFalse(firstFollowTable.firstOf(new NonTerminal("whileStatement")).isEmpty());

		assertFalse(firstFollowTable.firstOf(new NonTerminal("blockStatement")).isEmpty());

		
		final PromisseSet<MatchableProduction> firstOf = new RealizedPromisseSet<MatchableProduction>(
				Identifier.instance(),
				Text.instance(),
				Numeric.instance(),
				Terminal.of("this"),
				Terminal.of("super"),
				Terminal.of("null"),
				Terminal.of("new"),
				Terminal.of("true"),
				Terminal.of("false"),
				Terminal.of("("),
				Terminal.of("++"),
				Terminal.of("--"),
				Terminal.of("+"),
				Terminal.of("-"),
				Terminal.of("~"),
				Terminal.of("!")
		);
		
		assertEquals(firstOf , firstFollowTable.firstOf(new NonTerminal("expression")));
		
		final PromisseSet<MatchableProduction> followOf = new RealizedPromisseSet<MatchableProduction>(
				Terminal.of(")"), // grouping , if, while, other control structures
				Terminal.of("]"), // indexed access
				Terminal.of(":"), // ternary
				Terminal.of(";"), // inicialization
				Terminal.of(",") // argument list
		);
		
		assertEquals(followOf , firstFollowTable.followOf(new NonTerminal("expression")));
		
		
	}
	


	
	

	@Test  @Ignore
	public void testIsNotLRZero() {

		LenseGrammar g = new LenseGrammar();
		
		LookupTable table = new LRZeroAutomatonFactory().create().produceLookupTable(g);
		
		assertNotNull(table);
		
		int conflit = 0;
		for (LookupTableRow r : table){
			for (Map.Entry<Production, LookupTableAction> cell : r){
				LookupTableAction action = cell.getValue();
				if (action instanceof SplitAction){
					//System.out.println("Conflict at state " + r.toString() + " when next is " + cell.getKey().toString());  
					conflit++;
				}
			}
		}
		
		assertFalse(0 == conflit);
		
	}
	
	@Test @Ignore
	public void testIsNotSLROne() {

		LenseGrammar g = new LenseGrammar();
		
		LookupTable table = new SLRAutomatonFactory().create().produceLookupTable(g);
		
		assertNotNull(table);
		
		int conflit = 0;
		for (LookupTableRow r : table){
			for (Map.Entry<Production, LookupTableAction> cell : r){
				LookupTableAction action = cell.getValue();
				if (action instanceof SplitAction){
					//System.out.println("Conflict at state " + r.toString() + " when next is " + cell.getKey().toString());  
					conflit++;
				}
			}
		}
		

		
		assertFalse(0 == conflit);
		
	}
	@Test
	public void testLambda() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/lambda.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/lambda.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
	//	compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	@Test
	public void testOptionalType() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/optionaltype.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/optionaltype.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	@Test
	public void testStringInterpolation() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/interpolation.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/interpolation.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	@Test 
	public void testMaybeAssingment() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/maybeTest.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/maybeTest.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	@Test 
	public void testVariance (){
		final LenseTypeSystem instance = LenseTypeSystem.getInstance();
		TypeDefinition maybeAny = instance.specify(LenseTypeSystem.Maybe()  , LenseTypeSystem.Any());
		TypeDefinition none = LenseTypeSystem.None();
		TypeDefinition maybeWhole = instance.specify(LenseTypeSystem.Maybe()  , LenseTypeSystem.Whole());
		TypeDefinition maybeNatural = instance.specify(LenseTypeSystem.Maybe()  , LenseTypeSystem.Natural());
		
		assertTrue( instance.isAssignableTo(maybeNatural, maybeAny));
		assertFalse(instance.isAssignableTo(maybeAny,maybeNatural));
		
		assertTrue( instance.isAssignableTo(maybeNatural, maybeWhole));
		assertFalse(instance.isAssignableTo(maybeWhole,maybeNatural));
		
		assertTrue( instance.isAssignableTo(none, maybeAny));
		assertTrue( instance.isAssignableTo(none, maybeWhole));
		assertTrue( instance.isAssignableTo(none, maybeNatural));
		
	}
	
	@Test 
	public void testIllegalSpecification (){
		// TODO should not be possible to create a maybe of a maybe
		final LenseTypeSystem instance = LenseTypeSystem.getInstance();
		instance.specify(LenseTypeSystem.Maybe()  , LenseTypeSystem.None());
	}
	
	@Test 
	public void testField() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/field.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/field.java");

		assertTrue("File does not exist", file.exists());
		
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	
	@Test 
	public void testCompileExpression() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/expressions.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/expressions.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	@Test 
	public void testCompileNativeClass() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/collections/Array.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/collections/Array.java");

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	@Test 
	public void testCompileGenericClass() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/collections/Sequence.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/collections/Sequence.java");

		assertTrue("File does not exist", file.exists());
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
//	@Test 
//	public void testlenseType() throws IOException {
//		 
//		TypeDefinition sMaybe = LenseTypeSystem.getInstance().specify(LenseTypeSystem.Maybe(), LenseTypeSystem.String());
//				
//		MethodSignature mapSignature = new MethodSignature(
//				sMaybe, 
//				"map", 
//				new MethodParameter(lenseType.Function1.of(lenseType.Natural, lenseType.String), "it")
//		);
//		
//		Optional<Method> mapMethod = sMaybe.getAppropriateMethod(mapSignature);
//		
//		lenseType binded = (lenseType) mapMethod.get().bindGenerics(mapSignature);
//		
//		Method m = binded.getAppropriateMethod(mapSignature).get();
//		
//		assertNotNull(m);
//		assertEquals(LenseTypeSystem.getInstance().specify(LenseTypeSystem.Maybe(), LenseTypeSystem.Natural()), m.getReturningType().getUpperbound());
//	}
	
	@Test 
	public void testVoid() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/void.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/void.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);

	}
	
	
	@Test 
	public void testCompilerProgram() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/program.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/out/program.java");

		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.addBackEnd(new OutToJavaSource(out));
		compiler.compile(unitSet);
	}
	
	@Test  
	public void testCompilerInterface() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/Comparable.lense");
		
		assertTrue("File does not exist", file.exists());
		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

 
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new PrintOutBackEnd());
		compiler.compile(unitSet);
	}
	
	@Test  @Ignore
	public void testCompileLibrary() throws IOException {
		File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/lense/");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/compiled");
		
		assertTrue("File does not exist", folder.exists());
		
		final Compiler compiler = new LenseCompiler();
		compiler.addBackEnd(new OutToJavaSource(out));

		CompilationUnitSet unitSet = new FolderCompilationUnionSet(folder , name -> name.endsWith(".lense"));
	

		compiler.compile(unitSet);
	}


}
