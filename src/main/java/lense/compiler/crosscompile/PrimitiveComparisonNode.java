/**
 * 
 */
package lense.compiler.crosscompile;

import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public class PrimitiveComparisonNode extends ExpressionNode {

	
	private Operation operation;

	public TypeVariable getTypeVariable() {
        return  this.operation == Operation.Compare 
                ? LenseTypeSystem.Comparison()
                : LenseTypeSystem.Boolean();
    }
	
	/**
	 * Constructor.
	 * @param resolveComparisonOperation
	 */
	public PrimitiveComparisonNode(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}
	

	public ExpressionNode getLeft(){
		return (ExpressionNode) this.getChildren().get(0);
	}
	
	public ExpressionNode getRight(){
		return (ExpressionNode) this.getChildren().get(1);
	}
}
