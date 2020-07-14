/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.utils.Strings;

/**
 * 
 */
public final class OutToJavaSource implements CompilerBackEnd {

	FileLocations out;

	/**
	 * Constructor.
	 * @param out
	 * @throws IOException 
	 */
	public OutToJavaSource(FileLocations out) {
		this.out = out; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void use(CompiledUnit unit) {
		toSource(unit);
	}


	protected List<File> toSource(CompiledUnit unit) {

		List<File> files = new LinkedList<>();

		for (AstNode node : unit.getAstRootNode().getChildren()) {

			File target = out.getTargetFolder();
			File compiled = target;
			if (target.isDirectory()){

				if (!(node instanceof ClassTypeNode)){
					continue;
				}
				ClassTypeNode t = (ClassTypeNode)node;

				if (t.isNative()){
					continue;
				}

				String[] names = Strings.split(t.getFullname(), ".");
				
				if (t.getKind().isObject()) {
					names[names.length -1 ] = Strings.cammelToPascalCase(names[names.length - 1]);
				}
				
				String path =  Strings.join(names, "/");
				int pos = path.lastIndexOf('/');
				String filename = path.substring(pos+1) + ".java";
				File folder;
				if (pos >=0){
					path = path.substring(0, pos);
					folder = new File(target, path );
				} else {
					folder = target;
				}

				folder.mkdirs();

				compiled = new File(folder, filename);
				try {
					compiled.createNewFile();

				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			try(PrintWriter writer = new PrintWriter(new FileWriter(compiled))){
				TreeTransverser.transverse(node, new JavaSourceWriterVisitor(writer));
				files.add(compiled);
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		return files;
	}

}
