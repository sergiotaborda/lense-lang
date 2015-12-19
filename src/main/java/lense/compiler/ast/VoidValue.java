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
public class VoidValue extends LiteralExpressionNode {

	public TypeDefinition getTypeDefinition() {
		return LenseTypeSystem.Void();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return "void";
	}
}
