package lense.compiler.ir.tac;

public abstract class AbstractAssignInstruction extends TacInstruction{

	private Reference target;

	public AbstractAssignInstruction (Reference target){
		this.target = target;
	}
	
	public String toString(){
		return target.toString() + " := ";
	}
	
	public Reference getTarget() {
		return target;
	}
}
