/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public abstract class BooleanExpressionNode extends ExpressionNode {

	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(LenseTypeSystem.Boolean());
	}

}
