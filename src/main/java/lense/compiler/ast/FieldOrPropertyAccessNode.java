/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.NeedTypeCalculationNode;
import compiler.syntax.AstNode;
import compiler.typesystem.TypeDefinition;



/**
 * 
 */
public class FieldOrPropertyAccessNode extends NeedTypeCalculationNode {

	private AstNode primary;
	private String name;


	/**
	 * Constructor.
	 * @param string
	 */
	public FieldOrPropertyAccessNode(String name) {
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

	public void setType(TypeDefinition type){
		super.setTypeDefinition(type);
	}
	
	public AstNode getPrimary() {
		return primary;
	}
	public void setPrimary(AstNode primary) {
		this.primary = primary;
		this.add(primary);
	}

}
