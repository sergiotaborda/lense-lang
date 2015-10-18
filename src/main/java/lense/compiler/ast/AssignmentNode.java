/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.TypedNode;
import compiler.syntax.AstNode;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class AssignmentNode extends ExpressionNode {

	public enum Operation {
		SimpleAssign ("="), 
		MultiplyAndAssign("*="), 
		DivideAndAssign ("/="), 
		RemainderAndAssign ("%="), 
		AddAndAssign("+="), 
		SubtractAndAssign ("-="), 
		LeftShiftAndAssign ("<<="),
		RightShiftAndAssign(">>="), 
		PositiveRightShiftAndAssign(">>>="), 
		BitAndAndAssign ("&="),
		BitXorAndAssign ("^="), 
		BitOrAndAssign ("|=");

		private String symbol;

		private Operation (String symbol){
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
	private AstNode left;
	private ExpressionNode right;
	
	/**
	 * Constructor.
	 * @param resolveAssignmentOperation
	 */
	public AssignmentNode(Operation operation) {
		this.operation = operation;
	}
	
	public TypeDefinition getTypeDefinition() {
		return right.getTypeDefinition();
	}
	
	/**
	 * @param astNode
	 */
	public void setLeft(AstNode left) {
		this.left = left;
		this.add(left);
	}

	/**
	 * @param astNode
	 */
	public void setRight(ExpressionNode right) {
		this.right = right;
		this.add(right);
	}

	/**
	 * @return
	 */
	public TypedNode getLeft() {
		return (TypedNode)this.left;
	}

	/**
	 * @return
	 */
	public ExpressionNode getRight() {
		return this.right;
	}

	/**
	 * @return
	 */
	public Operation getOperation() {
		return operation;
	}

	public void replace(AstNode node, AstNode newnode){
		super.replace(node, newnode);
		
		if (this.left == node){
			this.left = newnode;
		} else if (this.right == node){
			this.right = (ExpressionNode) newnode;
		}
	}
}