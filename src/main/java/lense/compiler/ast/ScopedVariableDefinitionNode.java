/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.context.VariableInfo;
import lense.compiler.typesystem.Imutability;


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
	ExpressionNode getInitializer();

	
	void setInitializer(ExpressionNode node);

	/**
	 * @param info
	 */
	void setInfo(VariableInfo info);
	
    Imutability getImutability();
    
    public TypeNode getTypeNode();
}
