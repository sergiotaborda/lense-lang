/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.ExpressionNode;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class BooleanExpressionNode extends ExpressionNode {

	public TypeDefinition getTypeDefinition() {
		return LenseTypeSystem.Boolean();
	}

}
