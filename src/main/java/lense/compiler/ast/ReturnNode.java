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
public class ReturnNode extends StatementNode implements TypedNode {

	private TypeVariable expectedType;
	
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
	public TypeVariable getTypeVariable() {
		return this.getChildren().isEmpty() ? new FixedTypeVariable(LenseTypeSystem.Void()) : getValue().getTypeVariable();
	}
	
	@Override
	public void setTypeVariable(TypeVariable typeVariable) {
		getValue().setTypeVariable(typeVariable);
	}


	public TypeVariable getExpectedType() {
		return expectedType;
	}


	public void setExpectedType(TypeVariable returnType) {
		this.expectedType = returnType;
	}
}
