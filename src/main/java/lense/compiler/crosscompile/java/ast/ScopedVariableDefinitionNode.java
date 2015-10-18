/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import compiler.typesystem.VariableInfo;


/**
 * 
 */
public interface ScopedVariableDefinitionNode extends TypedNode {

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	TypedNode getInitializer();

	
	void setInitializer(ExpressionNode node);

	/**
	 * @param info
	 */
	void setInfo(VariableInfo info);
	
    Imutability getImutabilityValue();
}
