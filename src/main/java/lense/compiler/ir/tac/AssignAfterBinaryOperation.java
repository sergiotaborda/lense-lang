package lense.compiler.ir.tac;

public class AssignAfterBinaryOperation extends AbstractAssignInstruction {

	
	private Reference left;
	private Reference right;
	private Operation operation;

	public AssignAfterBinaryOperation (Reference target, Reference left, Operation operation, Reference right){
		super (target);
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	
	public String toString(){
		return super.toString()  + left.toString() + " " + operation.name() + " " + right.toString();
	}
	
	@Override
	public boolean replace(Reference find, Reference replacement) {
		boolean changed = false;
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

}
