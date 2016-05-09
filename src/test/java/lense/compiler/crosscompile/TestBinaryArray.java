package lense.compiler.crosscompile;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import compiler.FileCompilationUnit;
import compiler.ListCompilationUnitSet;
import lense.compiler.LenseSourceCompiler;

public class TestBinaryArray {

	@Test
	public void testByteArray()  {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/sdk/source/lense/core/lang/BitArray.lense");
		
		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendToList();
	}
	
	@Test
	public void testLinkedList()  {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/sdk/source/lense/core/collections/LinkedList.lense");
		
		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendToList();
	}
}
