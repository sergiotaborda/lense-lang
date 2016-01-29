/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;


/**
 * 
 */
public class VariableReadNode extends ExpressionNode {

	private String name;
	private VariableInfo variableInfo;
	
	public VariableReadNode() {
		this.name = "";
	}
	
	/**
	 * Constructor.
	 * @param id
	 */
	public VariableReadNode(String name) {
		this.name = name;
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
	
	public VariableInfo getVariableInfo(){
		return variableInfo;
	}

	public TypeDefinition getTypeDefinition(){
		return variableInfo.getTypeDefinition();
	}

}
