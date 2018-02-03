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

	public BooleanValue() {}
	public BooleanValue(boolean value) {
		this.setValue(value);
	}
	private boolean value;
	public TypeVariable getTypeVariable() {
		return LenseTypeSystem.Boolean();
	}
	public boolean isValue() {
		return value;
	}
	public void setValue(boolean value) {
		this.value = value;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return value ? "true" : "false";
	}
	
	public String toString(){
	    return  value ? "true" : "false";
	}

}
