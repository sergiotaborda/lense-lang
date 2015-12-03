/**
 * 
 */
package lense.compiler;

import java.io.File;

import compiler.AstCompiler;
import compiler.FileCompilationUnit;
import compiler.ListCompilationUnitSet;
import compiler.bnf.BnfCompiler;
import compiler.bnf.ToJavaBackEnd;

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

		final AstCompiler compiler = new BnfCompiler();

		try {
			compiler.parse(unitSet).sendTo(new ToJavaBackEnd(javaOut, "lense.compiler.AbstractLenseGrammar"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
