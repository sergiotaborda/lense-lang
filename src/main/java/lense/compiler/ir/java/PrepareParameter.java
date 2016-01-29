package lense.compiler.ir.java;

import lense.compiler.ir.tac.Operand;
import lense.compiler.ir.tac.TacInstruction;

public class PrepareParameter extends TacInstruction {

	private Operand right;

	public PrepareParameter(Operand right){
		this.right = right;
	}

	
	public String toString(){
		return "prepare param " + right.toString();
	}
	
	@Override
	public boolean replace(Operand find, Operand replacement) {
		if (find.equals(right)){
			this.right = replacement;
			return true;
		}
		return false;
	}


	public Operand getRight() {
		return right;
	}




}
