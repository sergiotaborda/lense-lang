/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;


/**
 * 
 */
public class VariableReadNode extends ExpressionNode {

	private String name;
	private VariableInfo variableInfo;
	
	/**
	 * Constructor.
	 * @param id
	 */
	public VariableReadNode(String name) {
		this.name = name;
	}
	

	/**
	 * Constructor.
	 * @param string
	 * @param info
	 */
	public VariableReadNode(String name, VariableInfo info) {
		this(name);
		variableInfo = info;
	}


	/**
	 * @param object
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param variableInfo
	 */
	public void setVariableInfo(VariableInfo variableInfo) {
		this.variableInfo = variableInfo;
	}

	public TypeDefinition getTypeDefinition(){
		return variableInfo.getTypeDefinition();
	}

}
