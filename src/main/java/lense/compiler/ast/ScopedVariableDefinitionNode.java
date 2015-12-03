/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.Imutability;
import lense.compiler.ast.TypedNode;
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
	ExpressionNode getInitializer();

	
	void setInitializer(ExpressionNode node);

	/**
	 * @param info
	 */
	void setInfo(VariableInfo info);
	
    Imutability getImutabilityValue();
    
    public TypeNode getTypeNode();
}
