package lense.compiler.crosscompile;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import compiler.FileCompilationUnit;
import compiler.ListCompilationUnitSet;
import lense.compiler.LenseSourceCompiler;

public class TestConstrutors {

	@Test @Ignore
	public void testConstrutors()  {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/construtors.lense");
		File klass = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/");

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendToList();
	}
}
