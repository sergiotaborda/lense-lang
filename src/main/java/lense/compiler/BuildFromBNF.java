/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.IOException;

import compiler.Compiler;
import compiler.FileCompilationUnit;
import compiler.bnf.BnfCompiler;
import compiler.bnf.ToJavaBackEnd;
import compiler.lexer.ListCompilationUnitSet;

/**
 * 
 */
public class BuildFromBNF {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/resources/lense/lense.bnf");
		File javaOut = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/java/lense/compiler/AbstractLenseGrammar.java");

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new FileCompilationUnit(file));


		final Compiler compiler = new BnfCompiler();
		compiler.addBackEnd(new ToJavaBackEnd(javaOut, "lense.compiler.AbstractLenseGrammar"));
		try {
			compiler.compile(unitSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
