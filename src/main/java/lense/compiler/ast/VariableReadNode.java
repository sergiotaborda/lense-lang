/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.context.VariableInfo;
import lense.compiler.type.variable.TypeVariable;


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

	public VariableInfo getVariableInfo(){
		return this.variableInfo;
	}
	
	public TypeVariable getTypeVariable(){
		if (variableInfo == null){
			return null;
		}
		return variableInfo.getTypeVariable();
	}
	
	public void setTypeVariable(TypeVariable type) {
		variableInfo.setTypeVariable(type);
	}
}
