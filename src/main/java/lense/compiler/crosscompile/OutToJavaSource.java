/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lense.compiler.ast.ClassTypeNode;
import lense.compiler.crosscompile.java.JavaSourceWriterVisitor;

import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;

/**
 * 
 */
public class OutToJavaSource implements CompilerBackEnd {

	File out;
	
	/**
	 * Constructor.
	 * @param out
	 * @throws IOException 
	 */
	public OutToJavaSource(File out) {
		this.out = out; 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void use(AstNode root) {
		

		AstNode javaRoot = TreeTransverser.transform(root, new Lense2JavaTransformer());
		
		File compiled = out;
		if (out.isDirectory()){
			ClassTypeNode t = (ClassTypeNode) root.getChildren().get(0);
			String path = t.getName().replace('.', '/');
			int pos = path.lastIndexOf('/');
			String filename = path.substring(pos+1) + ".java";
			path = path.substring(0, pos);
			File folder = new File(out, path );
			folder.mkdirs();
			
			compiled = new File(folder, filename);
			try {
				compiled.createNewFile();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		try(PrintWriter writer = new PrintWriter(new FileWriter(compiled))){
			TreeTransverser.tranverse(javaRoot, new JavaSourceWriterVisitor(writer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
