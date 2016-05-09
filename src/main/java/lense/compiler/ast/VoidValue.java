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
public class VoidValue extends LiteralExpressionNode {

	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(LenseTypeSystem.Void());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return "void";
	}
}
