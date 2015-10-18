/**
 * 
 */
package lense.compiler.crosscompile.java.ast;




/**
 * 
 */
public class ImportNode extends JavaAstNode {

	
	private QualifiedNameNode name;

	public QualifiedNameNode getName() {
		return name;
	}

	public void setName(QualifiedNameNode name) {
		this.name = name;
	}
}
