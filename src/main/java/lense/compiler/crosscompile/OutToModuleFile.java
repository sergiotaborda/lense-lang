/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import lense.compiler.ast.ModuleNode;
import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;

/**
 * 
 */
public class OutToModuleFile implements CompilerBackEnd{

	
	private File out;
	public OutToModuleFile(File out){
		this.out = out;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void use(AstNode root) {
		ModuleNode module  = (ModuleNode) root.getChildren().get(0);
		
		Properties p = new Properties();
		
		p.put("module.name", module.getName());
		p.put("module.version", module.getVersion());
		
		try {
			p.store(new FileOutputStream(out), "Lense module definition");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
