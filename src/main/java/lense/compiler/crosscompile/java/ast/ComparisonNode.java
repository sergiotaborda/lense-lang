/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import lense.compiler.crosscompile.java.JavaType;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class ComparisonNode extends BooleanExpressionNode {

	public enum Operation {
		LessThan ("<"),
		GreaterThan (">"),
		LessOrEqualTo ("<="),
		GreaterOrEqualTo(">="), 
		EqualTo ("=="), 
		Different ("!="),
		ReferenceEquals ("==="),
		ReferenceDifferent ("!==");
		private String symbol;

		Operation(String symbol){
			this.symbol = symbol;
		}

		/**
		 * @return
		 */
		public String symbol() {
			return symbol;
		}
	}
	
	private Operation operation;

	public ComparisonNode() {
		this(Operation.EqualTo);
	}
	
	/**
	 * Constructor.
	 * @param resolveComparisonOperation
	 */
	public ComparisonNode(Operation operation) {
		this.operation = operation;
	}

	public Operation getOperation() {
		return operation;
	}
	
	public TypeDefinition getTypeDefinition() {
		return JavaType.Boolean;
	}

	public ExpressionNode getLeft(){
		return (ExpressionNode) this.getChildren().get(0);
	}
	
	public ExpressionNode getRight(){
		return (ExpressionNode) this.getChildren().get(1);
	}
}
