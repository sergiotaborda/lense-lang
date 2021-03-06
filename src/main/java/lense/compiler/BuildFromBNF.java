/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.PrintWriter;

import compiler.AstCompiler;
import compiler.ListCompilationUnitSet;
import compiler.SourceFileCompilationUnit;
import compiler.bnf.BnfCompiler;
import compiler.bnf.RuleRef;
import compiler.bnf.ToJavaBackEnd;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourcePath;

/**
 * 
 */
public class BuildFromBNF {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		var rootFolder = DiskSourceFileSystem.instance().folder(new File(".").getAbsoluteFile().getParentFile());
		
		var file =  rootFolder.file(SourcePath.of("src","main","resources","lense", "lense.bnf")); 

		var javaOut =  rootFolder.file(SourcePath.of("src","main","java","lense","compiler", "AbstractLenseGrammar.java")); 

		ListCompilationUnitSet unitSet = new ListCompilationUnitSet();
		unitSet.add(new SourceFileCompilationUnit(file));

		final AstCompiler compiler = new BnfCompiler();

		try {
			compiler.parse(unitSet).sendTo(new ToJavaBackEnd(javaOut, "lense.compiler.AbstractLenseGrammar"){
				
				@Override
				protected void writeRule(PrintWriter writer, RuleRef rule) {
					
					 if (rule.getName().equals("versionLiteral")){
							writer.append("VersionLiteral.instance()");
					} else {
						super.writeRule(writer, rule);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}

}
