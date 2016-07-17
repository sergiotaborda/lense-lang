package lense.compiler.crosscompile;

import java.io.IOException;

import org.junit.Test;

import compiler.ListCompilationUnitSet;
import compiler.StringCompilationUnit;
import lense.compiler.LenseSourceCompiler;

public class TestParsing {

	@Test
	public void testLiteralSequence() throws IOException {


		
		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new StringCompilationUnit(
		"public class Test { " +
		" public void test(){" +
		" var String s = \"Hello array {{ [1, 2] }} \"" +
		"}" + 
		"}"
		));

		new LenseSourceCompiler().parse(unitSet).sendToList();

	}

}
