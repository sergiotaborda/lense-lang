package lense.compiler.ir.tac;

public class UnaryOperation extends TacInstruction {

	
	private Address right;
	private Operation operation;

	public UnaryOperation ( Operation operation, Address right){
		this.right = right;
		this.operation = operation;
	}
}
