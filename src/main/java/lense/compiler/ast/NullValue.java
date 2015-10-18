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
public class NullValue extends LiteralExpressionNode {

	public TypeDefinition getTypeDefinition() {
		return LenseTypeSystem.None();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return "null";
	}
}
