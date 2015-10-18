/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.LiteralExpressionNode;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class BooleanValue extends LiteralExpressionNode {

	
	private boolean value;
	public TypeDefinition getTypeDefinition() {
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
}
