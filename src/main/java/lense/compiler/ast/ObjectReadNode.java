/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;


/**
 * 
 */
public class ObjectReadNode extends ExpressionNode {

	private TypeDefinition type;
	private String objName;
	/**
	 * Constructor.
	 * @param id
	 */
	public ObjectReadNode(TypeDefinition type, String objName) {
		this.type = type;
		this.objName = objName;
	}
	
	public TypeVariable getTypeVariable(){
		if (type == null){
			return null;
		}
		return new lense.compiler.type.variable.FixedTypeVariable(type);
	}

	public String getObjectName() {
		return objName;
	}


}
