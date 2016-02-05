package lense.compiler.ir.tac;

public abstract class AbstractAssignInstruction extends TacInstruction{

	private Operand target;

	public AbstractAssignInstruction (Operand target){
		this.target = target;
	}
	
	public String toString(){
		return super.toString() + target.toString() + " := ";
	}
	
	public Operand getTarget() {
		return target;
	}
	
	public boolean replace(Operand find, Operand replacement) {
		if (target.equals(find)){
			target = replacement;
			return true;
		}
		return false;
	}
}
