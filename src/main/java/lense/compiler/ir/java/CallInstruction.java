package lense.compiler.ir.java;

import lense.compiler.ir.tac.Reference;
import lense.compiler.ir.tac.TacInstruction;

public class CallInstruction extends TacInstruction {

	private String methodName;
	private Reference target;

	public CallInstruction(Reference target, String name) {
		this.methodName = name;
		this.target = target;
	}
	
	public String toString(){
		return target.toString() + " := call " + methodName; 
	}

	@Override
	public boolean replace(Reference find, Reference replacement) {
		if (target.equals(find)){
			target = replacement;
			return true;
		}
		return false;
	}

}
