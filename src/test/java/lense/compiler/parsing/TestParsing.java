package lense.compiler.parsing;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import compiler.ListCompilationUnitSet;
import compiler.SourceFileCompilationUnit;
import compiler.StringCompilationUnit;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFolder;
import compiler.filesystem.SourcePath;
import lense.compiler.LenseSourceCompiler;

public class TestParsing {


	SourceFolder baseFolder = DiskSourceFileSystem.instance().folder(new File(".").getAbsoluteFile().getParentFile());
	
	

	@Test @Ignore
	public void testLiteralSequence() throws IOException {


		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new StringCompilationUnit(
		"public class Test { " +
		" public function test() : Void {" +
		" var  s : String = \"Hello array {{ [1, 2] }} \";" +
		"}" + 
		"}"
		));

		new LenseSourceCompiler().parse(unitSet).sendToList();

	}

	//@Test(timeout = 10000)
	@Test @Ignore
	public void testDoubleIncrement() throws IOException {


		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new StringCompilationUnit(
		"public class Test { " +
		" public test() : Void{" +
		" var  i : Integer = 3;" +
		" var  j : Integer = (i++)++;" + 
		"}}"
		));

		new LenseSourceCompiler().parse(unitSet).sendToList();

	}

	@Test @Ignore
	public void testByteArray()  {
		var file =  baseFolder.file(SourcePath.of("src","main","sdk","source","lense","code","lang", "BitArray.lense")); 

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendToList();
	}
	
	@Test @Ignore
	public void testLinkedList()  {
		var file =  baseFolder.file(SourcePath.of("src","main","sdk","source","lense","code","collections", "LinkedList.lense")); 

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendToList();
	}
}
