package lense.compiler.ir.tac;

import lense.compiler.ir.Operation;

public class AssignAfterUnaryOperation extends AbstractAssignInstruction {

	
	private Operand right;
	private Operation operation;

	public AssignAfterUnaryOperation (Operand target, Operation operation, Operand right){
		super(target);
		this.right = right;
		this.operation = operation;
	}
	
	public String toString(){
		return super.toString() + operation.name() + " " + right.toString();
	}
	
	@Override
	public boolean replace(Operand find, Operand replacement) {
		boolean changed = super.replace(find, replacement);
		
		if (this.right.equals(find)){
			this.right = replacement;
			changed = true;
		}
		return changed;
	}

	public Operand getRight() {
		return right;
	}

	public Operation getOperation() {
		return operation;
	}
}
