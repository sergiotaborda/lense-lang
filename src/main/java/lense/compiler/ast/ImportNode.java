/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.LenseAstNode;



/**
 * 
 */
public class ImportNode extends LenseAstNode {

	
	private QualifiedNameNode name;

	public QualifiedNameNode getName() {
		return name;
	}

	public void setName(QualifiedNameNode name) {
		this.name = name;
	}
}
