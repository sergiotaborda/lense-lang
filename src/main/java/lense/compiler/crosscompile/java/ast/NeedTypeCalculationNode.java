/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class NeedTypeCalculationNode extends ExpressionNode {

	public void setTypeDefinition(TypeDefinition type) {
		this.type = type;
	}
}
