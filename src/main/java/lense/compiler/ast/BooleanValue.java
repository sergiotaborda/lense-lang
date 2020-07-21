/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class BooleanValue extends LiteralExpressionNode {

	private boolean value;
	
	public BooleanValue() {}
	
	public BooleanValue(boolean value) {
		this.setValue(value);
	}
	
	public TypeVariable getTypeVariable() {
		return LenseTypeSystem.Boolean();
	}
	
	public void setTypeVariable(TypeVariable type) {
		super.setTypeVariable(type);
	}
	
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public String getLiteralValue() {
		return value ? "true" : "false";
	}
	
	public String toString(){
	    return  value ? "true" : "false";
	}

	
	public BooleanValue negate() {
		return new BooleanValue(!this.value);
	}

}
