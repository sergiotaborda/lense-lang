package lense.compiler.crosscompile;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import compiler.FileCompilationUnit;
import compiler.ListCompilationUnitSet;
import lense.compiler.FileLocations;
import lense.compiler.LenseSourceCompiler;
import lense.compiler.crosscompile.java.OutToJavaSource;

public class TestIntermediaryRepresentation {

	@Test @Ignore
	public void testCompileExpression() throws IOException {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/arithmetic.lense");
		File out = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/");

		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(new OutToJavaSource(new FileLocations(out, null, null,null) ));
	}
}
