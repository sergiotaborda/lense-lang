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
public abstract class BooleanExpressionNode extends ExpressionNode {

	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(LenseTypeSystem.Boolean());
	}

}
