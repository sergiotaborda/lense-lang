/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;



/**
 * 
 */
public class PropertyAccessNode extends NeedTypeCalculationNode {

	private AstNode primary;
	private String name;


	/**
	 * Constructor.
	 * @param string
	 */
	public PropertyAccessNode(String name) {
		setName(name);
	}

	/**
	 * @param string
	 */
	public void setName(String name) {
		if (name == null){
			throw new IllegalArgumentException();
		}
		this.name= name;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setType(TypeVariable type){
		super.setTypeVariable(type);
	}
	
	public AstNode getPrimary() {
		return primary;
	}
	public void setPrimary(AstNode primary) {
		this.primary = primary;
		this.add(primary);
	}

}
