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
import java.util.Collection;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import compiler.AstCompiler;
import compiler.CompilerMessage;
import compiler.FirstFollowTable;
import compiler.FirstFollowTableCalculator;
import compiler.ListCompilationUnitSet;
import compiler.PrintOutBackEnd;
import compiler.PromisseSet;
import compiler.RealizedPromisseSet;
import compiler.SourceFileCompilationUnit;
import compiler.StringCompilationUnit;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFolder;
import compiler.filesystem.SourcePath;
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
import compiler.syntax.AstNode;
import lense.compiler.FileLocations;
import lense.compiler.Import;
import lense.compiler.LenseGrammar;
import lense.compiler.LenseLanguage;
import lense.compiler.LenseSourceCompiler;
import lense.compiler.PathPackageResolver;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.crosscompile.java.OutToJavaSource;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.repository.TypeRepository;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class TestLenseGrammar {

	SourceFolder baseFolder = DiskSourceFileSystem.instance().folder(new File(".").getAbsoluteFile().getParentFile());
	
	
	@Test
	@Ignore
	public void testFirstAndFollow() {

		LenseGrammar g = new LenseGrammar();

		FirstFollowTable firstFollowTable = new FirstFollowTableCalculator()
				.calculateFrom(g.getStartProduction());

		assertFalse(firstFollowTable.followOf(
				new NonTerminal("packageDeclaration")).isEmpty());
		assertFalse(firstFollowTable.followOf(
				new NonTerminal("superDeclaration")).isEmpty());
		assertFalse(firstFollowTable
				.followOf(new NonTerminal("blockStatement")).isEmpty());

		assertFalse(firstFollowTable
				.firstOf(new NonTerminal("typeDeclaration")).isEmpty());
		assertFalse(firstFollowTable.firstOf(new NonTerminal("type")).isEmpty());
		assertFalse(firstFollowTable.firstOf(
				new NonTerminal("localVariableDeclaration")).isEmpty());
		assertFalse(firstFollowTable.firstOf(new NonTerminal("whileStatement"))
				.isEmpty());

		assertFalse(firstFollowTable.firstOf(new NonTerminal("blockStatement"))
				.isEmpty());

		final PromisseSet<MatchableProduction> firstOf = new RealizedPromisseSet<MatchableProduction>(
				Identifier.instance(), Text.instance(), Numeric.instance(),
				Terminal.of("this"), Terminal.of("super"), Terminal.of("null"),
				Terminal.of("new"), Terminal.of("true"), Terminal.of("false"),
				Terminal.of("("), Terminal.of("++"), Terminal.of("--"),
				Terminal.of("+"), Terminal.of("-"), Terminal.of("~"),
				Terminal.of("!"));

		assertEquals(firstOf,
				firstFollowTable.firstOf(new NonTerminal("expression")));

		final PromisseSet<MatchableProduction> followOf = new RealizedPromisseSet<MatchableProduction>(
				Terminal.of(")"), // grouping , if, while, other control
									// structures
				Terminal.of("]"), // indexed access
				Terminal.of(":"), // ternary
				Terminal.of(";"), // inicialization
				Terminal.of(",") // argument list
		);

		assertEquals(followOf,
				firstFollowTable.followOf(new NonTerminal("expression")));

	}

	@Test
	@Ignore
	public void testIsNotLRZero() {

		LenseGrammar g = new LenseGrammar();

		LookupTable table = new LRZeroAutomatonFactory().create()
				.produceLookupTable(g);

		assertNotNull(table);

		int conflit = 0;
		for (LookupTableRow r : table) {
			for (Map.Entry<Production, LookupTableAction> cell : r) {
				LookupTableAction action = cell.getValue();
				if (action instanceof SplitAction) {
					// System.out.println("Conflict at state " + r.toString() +
					// " when next is " + cell.getKey().toString());
					conflit++;
				}
			}
		}

		assertFalse(0 == conflit);

	}

	@Test
	@Ignore
	public void testIsNotSLROne() {

		LenseGrammar g = new LenseGrammar();

		LookupTable table = new SLRAutomatonFactory().create()
				.produceLookupTable(g);

		assertNotNull(table);

		int conflit = 0;
		for (LookupTableRow r : table) {
			for (Map.Entry<Production, LookupTableAction> cell : r) {
				LookupTableAction action = cell.getValue();
				if (action instanceof SplitAction) {
					// System.out.println("Conflict at state " + r.toString() +
					// " when next is " + cell.getKey().toString());
					conflit++;
				}
			}
		}

		assertFalse(0 == conflit);

	}

	@Test @Ignore
	public void testLambda() throws IOException {
		
		
		var file = baseFolder.file(SourcePath.of("src","main","lense","lambda.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","lambda.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}


	@Test @Ignore
	public void testStringInterpolation() throws IOException {
		
		var file = baseFolder.file(SourcePath.of("src","main","lense","interpolation.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","interpolation.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testMaybeAssingment() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","maybeTest.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","maybeTest.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}
	
	@Test @Ignore
	public void testSequenceLiterals() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","literals.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","literals.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testVariance() {
		
		final var instance =  new LenseTypeAssistant(null);
		
		TypeDefinition maybeAny = instance.specify(LenseTypeSystem.Maybe(),
				LenseTypeSystem.Any());
		TypeDefinition none = LenseTypeSystem.None();
		TypeDefinition maybeWhole = instance.specify(LenseTypeSystem.Maybe(),
				LenseTypeSystem.Whole());
		TypeDefinition maybeNatural = instance.specify(LenseTypeSystem.Maybe(),
				LenseTypeSystem.Natural());

		assertTrue(instance.isAssignableTo(maybeNatural, maybeAny).matches() );
		assertFalse(instance.isAssignableTo(maybeAny, maybeNatural).matches() );

		assertTrue(instance.isAssignableTo(maybeNatural, maybeWhole).matches() );
		assertFalse(instance.isAssignableTo(maybeWhole, maybeNatural).matches() );

		assertTrue(instance.isAssignableTo(none, maybeAny).matches() );
		assertTrue(instance.isAssignableTo(none, maybeWhole).matches() );
		assertTrue(instance.isAssignableTo(none, maybeNatural).matches() );

	}

	@Test @Ignore
	public void testIllegalSpecification() {
		// TODO should not be possible to create a maybe of a maybe
		final LenseTypeSystem instance = LenseTypeSystem.getInstance();
		instance.specify(LenseTypeSystem.Maybe(), LenseTypeSystem.None());
	}

	@Test @Ignore
	public void testField() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","field.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","field.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testCompileExpression() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","expressions.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","expressions.java"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}
 
	@Test @Ignore
	public void testCompileNativeClass() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","Array.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","Array.java"));

		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));
	}

	@Test @Ignore
	public void testCompileGenericClass() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","Sequence.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","Sequence.java"));
		
		assertTrue("File does not exist", file.exists());
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testCompileModule() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","module.lense"));

		assertTrue("File does not exist", file.exists());
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		final AstCompiler compiler = new LenseSourceCompiler();
		compiler.parse(unitSet).sendTo(new PrintOutBackEnd());

	}

	@Test @Ignore
	public void testCompilePackage() throws IOException {

		ListCompilationUnitSet all = new ListCompilationUnitSet();

		StringBuilder builder = new StringBuilder(
				"import lense.lang.reflection.Package; import lense.lang.String;")
				.append("public class ").append(
						"Package$Info implements Package { "); // TODO
																// identifiers
																// should not
																// have $ or _

		builder.append("public String getName(){");
		builder.append(" return \"").append("lense.lang.math").append("\" ;");
		builder.append("}");
		builder.append("}");

		all.add(new StringCompilationUnit(builder.toString()));

		new LenseSourceCompiler().parse(all).sendToList();
	}

	@Test @Ignore
	public void testVoid() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","void.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","void.java"));
		

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testCompilerProgram() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","program.lense"));
		var out = baseFolder.file(SourcePath.of("src", "main","out","program.java"));
		
		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(
				new OutToJavaSource(new FileLocations(out.parentFolder(), null, null,null) ));

	}

	@Test @Ignore
	public void testCompilerInterface() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","Comparable.lense"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(new PrintOutBackEnd());

	}

	@Test @Ignore
	public void testNameResolution() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","testImports.lense"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		AstCompiler parser = new AstCompiler(new LenseLanguage());
		AstNode unitTypes = parser
				.parse(unitSet)
				.passBy(new NameResolutionPhase(TypeRepository.empty(), new PathPackageResolver(file.getPath()), new TestListener())).sendToList().get(0).getAstRootNode();

		UnitTypes t = (UnitTypes) unitTypes;

		ClassTypeNode n = t.getTypes().get(0);

		Collection<Import> imps = n.imports();

		assertNotNull(imps);
		assertEquals(14, imps.size());

	}

	@Test @Ignore
	public void testSequenceNameResolution() throws IOException {

		var file = baseFolder.file(SourcePath.of("src","main","lense","sequenceImports.lense"));

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		AstCompiler parser = new AstCompiler(new LenseLanguage());
		AstNode unitTypes = parser
				.parse(unitSet)
				.passBy(new NameResolutionPhase(TypeRepository.empty(), new PathPackageResolver(
						file.getPath()), new TestListener())).sendToList().get(0).getAstRootNode();

		UnitTypes t = (UnitTypes) unitTypes;

		ClassTypeNode n = t.getTypes().get(0);

		Collection<Import> imps = n.imports();

		assertNotNull(imps);
		assertEquals(2, imps.size());

	}

	public  class TestListener implements compiler.CompilerListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void start() {
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void error(CompilerMessage error) {
			throw new AssertionError(error.getMessage());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void warn(CompilerMessage error) {
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void end() {
			
		}

        @Override
        public void trace(CompilerMessage error) {
           
        }
		
	}
}
