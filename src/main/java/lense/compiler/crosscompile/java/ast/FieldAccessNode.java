/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.syntax.AstNode;



/**
 * 
 */
public class FieldAccessNode extends NeedTypeCalculationNode {

	private String name;
	private AstNode primary;

	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name= name;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	public AstNode getPrimary() {
		return primary;
	}
	public void setPrimary(AstNode primary) {
		this.primary = primary;
		this.add(primary);
	}
}
