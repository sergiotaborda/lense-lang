package lense.compiler.ir.tac;

public class BinaryOperation extends TacInstruction {

	
	private Address left;
	private Address right;
	private Operation operation;

	public BinaryOperation (Address left, Operation operation, Address right){
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
}
