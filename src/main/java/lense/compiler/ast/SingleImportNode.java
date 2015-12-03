/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.LenseAstNode;



/**
 * 
 */
public class SingleImportNode extends LenseAstNode {

	
	private String alias;
	private QualifiedNameNode name;

	public SingleImportNode (QualifiedNameNode name, String alias){
		this.name = name;
		this.alias= alias;
	}
	
	public SingleImportNode (QualifiedNameNode name){
		this.name = name;
	}
	
	public QualifiedNameNode getName(){
		return name;
	}
	
	/**
	 * @param lexicalValue
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias(){
		return alias;
	}
	

}
