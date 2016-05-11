/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class BooleanValue extends LiteralExpressionNode {

	
	private boolean value;
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable( LenseTypeSystem.Boolean());
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

}