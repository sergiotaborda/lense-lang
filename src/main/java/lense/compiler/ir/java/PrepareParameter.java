package lense.compiler.ir.java;

import lense.compiler.ir.tac.Reference;
import lense.compiler.ir.tac.TacInstruction;

public class PrepareParameter extends TacInstruction {

	private Reference right;

	public PrepareParameter(Reference right){
		this.right = right;
	}

	
	public String toString(){
		return "prepare param " + right.toString();
	}
	
	@Override
	public boolean replace(Reference find, Reference replacement) {
		if (find.equals(right)){
			this.right = replacement;
			return true;
		}
		return false;
	}

}
