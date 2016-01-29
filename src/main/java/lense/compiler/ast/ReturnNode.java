/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.StatementNode;
import lense.compiler.ast.TypedNode;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class ReturnNode extends StatementNode implements TypedNode {

	
	public ReturnNode(){
		
	}
	/**
	 * @param expressionNode
	 */
	public void setValue(ExpressionNode expressionNode) {
		this.add(expressionNode);
	}
	
	public ExpressionNode getValue(){
		return this.getChildren().isEmpty() ? null : (ExpressionNode)this.getChildren().get(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getTypeDefinition() {
		return this.getChildren().isEmpty() ? LenseTypeSystem.Void() : ((ExpressionNode)this.getChildren().get(0)).getTypeDefinition();
	}
}
