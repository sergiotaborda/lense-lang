/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
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


	protected List<SourceFile> toSource(CompiledUnit unit) {

		List<SourceFile> files = new LinkedList<>();

		for (AstNode node : unit.getAstRootNode().getChildren()) {

			var target = out.getTargetFolder();
			SourceFile  compiled = null;
			if (target.isFolder()){

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
				SourceFolder folder;
				if (pos >=0){
					path = path.substring(0, pos);
					folder = target.folder(path);
				} else {
					folder = target;
				}

				folder.ensureExists();

				compiled = folder.file(filename);
				compiled.ensureExists();

			

			}

			try(PrintWriter writer = new PrintWriter(compiled.writer())){
				TreeTransverser.transverse(node, new JavaSourceWriterVisitor(writer));
				node.setProperty("writen", Boolean.TRUE);
				files.add(compiled);
			} 
		}
		return files;
	}

}
