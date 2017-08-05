/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;

/**
 * 
 */
public class OutToJavaSource implements CompilerBackEnd {

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

	public File toSource(CompiledUnit unit) {
		
		//AstNode javaRoot = TreeTransverser.transform(unit.getAstRootNode(), new Lense2JavaTransformer());
		File target = out.getTargetFolder();
		File compiled = target;
		if (target.isDirectory()){
			AstNode node = unit.getAstRootNode().getChildren().get(0);
			
			if (!(node instanceof ClassTypeNode)){
				return null;
			}
			ClassTypeNode t = (ClassTypeNode)node;
			
			if (t.isNative()){
				return null;
			}
			
			String path = t.getName().replace('.', '/');
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
			TreeTransverser.transverse(unit.getAstRootNode(), new JavaSourceWriterVisitor(writer));
			return compiled;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
