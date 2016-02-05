/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lense.compiler.ast.ClassTypeNode;
import compiler.CompiledUnit;
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
	public void use(CompiledUnit unit) {
		

		AstNode javaRoot = TreeTransverser.transform(unit.getAstRootNode(), new Lense2JavaTransformer());
		
		File compiled = out;
		if (out.isDirectory()){
			AstNode node = unit.getAstRootNode().getChildren().get(0);
			
			if (!(node instanceof ClassTypeNode)){
				return;
			}
			ClassTypeNode t = (ClassTypeNode)node;
			String path = t.getName().replace('.', '/');
			int pos = path.lastIndexOf('/');
			String filename = path.substring(pos+1) + ".java";
			File folder;
			if (pos >=0){
				path = path.substring(0, pos);
				folder = new File(out, path );
			} else {
				folder = out;
			}
			
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
			TreeTransverser.transverse(javaRoot, new JavaSourceWriterVisitor(writer));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
