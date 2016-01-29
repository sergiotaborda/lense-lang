package lense.compiler.ir.tac;

import lense.compiler.ir.Operation;

public class AssignAfterBinaryOperation extends AbstractAssignInstruction {

	
	private Operand left;
	private Operand right;
	private Operation operation;

	public AssignAfterBinaryOperation (Operand target, Operand left, Operation operation, Operand right){
		super (target);
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	
	public String toString(){
		return super.toString()  + left.toString() + " " + operation.name() + " " + right.toString();
	}
	
	@Override
	public boolean replace(Operand find, Operand replacement) {
		
		boolean changed = super.replace(find, replacement);
		if (this.left.equals(find)){
			this.left = replacement;
			changed = true;
		}
		if (this.right.equals(find)){
			this.right = replacement;
			changed = true;
		}
		return changed;
	}

	public Operand getRight() {
		return right;
	}
	
	public Operand getLeft() {
		return left;
	}

	public Operation getOperation() {
		return operation;
	}

}
