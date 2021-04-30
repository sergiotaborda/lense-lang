/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class AssignmentNode extends ExpressionNode {

	public enum Operation {
		SimpleAssign ("=", false, null), 
		MultiplyAndAssign("*=", true, ArithmeticOperation.Multiplication), 
		DivideAndAssign ("/=",true, ArithmeticOperation.Division),
		IntegerDivideAndAssign ("\\=",true, ArithmeticOperation.IntegerDivision), 
		RemainderAndAssign ("%=",true, ArithmeticOperation.Remainder), 
		AddAndAssign("+=",true, ArithmeticOperation.Addition), 
		ConcatAndAssign("++=",true, ArithmeticOperation.Concatenation), 
		SubtractAndAssign ("-=",true, ArithmeticOperation.Subtraction), 
		LeftShiftAndAssign ("<<=",true, ArithmeticOperation.LeftShift),
		RightShiftAndAssign(">>=",true, ArithmeticOperation.RightShift), 
		PositiveRightShiftAndAssign(">>>=",true, ArithmeticOperation.SignedRightShift), 
		BitAndAndAssign ("&=",true, ArithmeticOperation.BitAnd),
		BitXorAndAssign ("^=",true, ArithmeticOperation.BitXor), 
		BitOrAndAssign ("|=",true, ArithmeticOperation.BitOr),
		ComplementAndAssign ("~=",true, ArithmeticOperation.Complement);

		private String symbol;
		private boolean isOperateAndAssign;
		private ArithmeticOperation arithmeticOperation;

		private Operation (String symbol, boolean isOperateAndAssign , ArithmeticOperation arithmeticOperation){
			this.symbol = symbol;
			this.isOperateAndAssign = isOperateAndAssign;
			this.arithmeticOperation = arithmeticOperation;
		}
		
		/**
		 * @return
		 */
		public String symbol() {
			return symbol;
		}

		public boolean isOperateAndAssign() {
			return isOperateAndAssign;
		}

		public ArithmeticOperation getArithmeticOperation() {
			return arithmeticOperation;
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
	
	public TypeVariable getTypeVariable() {
		return right.getTypeVariable();
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

	public String toString() {
		return this.getLeft().toString() + " <==" + operation.toString() + "= " + this.getRight().toString();
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
