/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.BooleanExpressionNode;
import lense.compiler.ast.ExpressionNode;




/**
 * 
 */
public class BooleanOperatorNode extends BooleanExpressionNode {

	public enum BooleanOperation {
		BitAnd ("&"),
		BitOr ("|"),
		BitXor("^"),
		LogicShortAnd("&&"),
		LogicShortOr("||"),
		LogicNegate("!"), 
		InstanceofType("instanceof"), 
		BitNegate("~");
		
		private String symbol;

		BooleanOperation(String symbol){
			this.symbol = symbol;
		}

		/**
		 * @return
		 */
		public String symbol() {
			return symbol;
		}
	}
	
	private BooleanOperation operation;

	/**
	 * Constructor.
	 * @param resolveBooleanOperation
	 */
	public BooleanOperatorNode(BooleanOperation operation) {
		this.operation = operation;
	}
	
	public ExpressionNode getLeft(){
		return (ExpressionNode) this.getChildren().get(0);
	}
	
	public ExpressionNode getRight(){
		return (ExpressionNode) this.getChildren().get(1);
	}

	/**
	 * @return
	 */
	public BooleanOperation getOperation() {
		return operation;
	}

}
