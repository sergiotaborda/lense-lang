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
public class NullValue extends LiteralExpressionNode {

	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable( LenseTypeSystem.None());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return "null";
	}
}
