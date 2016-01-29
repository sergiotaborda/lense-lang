package lense.compiler.ir.tac;

public class AssignAfterUnaryOperation extends AbstractAssignInstruction {

	
	private Reference right;
	private Operation operation;

	public AssignAfterUnaryOperation (Reference target, Operation operation, Reference right){
		super(target);
		this.right = right;
		this.operation = operation;
	}
	
	public String toString(){
		return super.toString() + operation.name() + " " + right.toString();
	}
	
	@Override
	public boolean replace(Reference find, Reference replacement) {
		if (this.right.equals(find)){
			this.right = replacement;
			return true;
		}
		return false;
	}
}
