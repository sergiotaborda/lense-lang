/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.IOException;

import lense.compiler.crosscompile.OutToJavaSource;
import lense.compiler.crosscompile.OutToModuleFile;
import compiler.CompilationUnitSet;
import compiler.FolderCompilationUnionSet;
import compiler.FileCompilationUnit;
import compiler.lexer.ListCompilationUnitSet;

/**
 * 
 */
public class LenseCompiler {


	public LenseCompiler (){}

	/**
	 * @param unitSet
	 */
	public void compile(ListCompilationUnitSet unitSet) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * @param moduleproject
	 * @throws IOException 
	 */
	public void compileModuleFromDirectory(File moduleproject){
		try {
			File sources = new File(moduleproject, "source");

			if (!sources.exists()){
				throw new CompilationError("No sources found. No folder " + sources.getAbsolutePath() +" exists");
			}

			File modulesOut = new File(moduleproject, "compilation/java/bin");
			if (!modulesOut.exists()){
				modulesOut.mkdirs();
			}
			
			File target = new File(moduleproject, "compilation/java/target");
			if (!target.exists()){
				target.mkdirs();
			}
			
			File moduleFile = new File(sources, "module.lense");

			if (!moduleFile.exists()){
				throw new CompilationError("Could not find module.lense file in the source root");
			}
			CompilationUnitSet moduleUnit = new FolderCompilationUnionSet(sources , name -> name.equals("module.lense"));
			LenseSourceCompiler compiler = new LenseSourceCompiler();

			File moduleProperties = new File(target, "module.properties");
			
			compiler.addBackEnd(new OutToModuleFile(moduleProperties));
			compiler.compile(moduleUnit);
			
			
			CompilationUnitSet unitSet = new FolderCompilationUnionSet(sources , name -> !name.equals("module.lense") && name.endsWith(".lense"));

		    compiler = new LenseSourceCompiler();

			compiler.addBackEnd(new OutToJavaSource(target));
			compiler.compile(unitSet);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
