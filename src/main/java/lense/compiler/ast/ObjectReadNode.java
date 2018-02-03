/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;


/**
 * 
 */
public class ObjectReadNode extends ExpressionNode {

	private TypeVariable type;
	private String objName;
	/**
	 * Constructor.
	 * @param id
	 */
	public ObjectReadNode(TypeVariable type, String objName) {
		this.type = type;
		this.objName = objName;
	}
	
	public TypeVariable getTypeVariable(){
		return type;
	}

	public String getObjectName() {
		return objName;
	}


}
