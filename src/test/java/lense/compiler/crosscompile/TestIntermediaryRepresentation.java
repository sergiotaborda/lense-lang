package lense.compiler.crosscompile;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import lense.compiler.LenseSourceCompiler;
import lense.compiler.ir.OutToIR;

import org.junit.Test;

import compiler.FileCompilationUnit;
import compiler.ListCompilationUnitSet;

public class TestIntermediaryRepresentation {

	@Test
	public void testCompileExpression() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/arithmetic.lense");

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(new OutToIR());

	}
}
