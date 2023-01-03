package lense.compiler.crosscompile;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import compiler.ListCompilationUnitSet;
import compiler.SourceFileCompilationUnit;
import compiler.filesystem.DiskSourceFileSystem;
import lense.compiler.FileLocations;
import lense.compiler.LenseSourceCompiler;
import lense.compiler.crosscompile.java.OutToJavaSource;

public class TestIntermediaryRepresentation {

	@Test @Ignore
	public void testCompileExpression() throws IOException {
		
		File target = new File(new File(".").getAbsoluteFile().getParentFile(),"src/main/lense/");
		
		var out = DiskSourceFileSystem.instance().folder(target);

		var file = out.file("arithmetic.lense");
		
		assertTrue("File does not exist", file.exists());

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		new LenseSourceCompiler().parse(unitSet).sendTo(new OutToJavaSource(new FileLocations(out, null, null,null) ));
	}
}
