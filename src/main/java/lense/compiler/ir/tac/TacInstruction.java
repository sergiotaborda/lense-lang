package lense.compiler.ir.tac;

import lense.compiler.ir.AbstractInstruction;
import lense.compiler.ir.stack.StackInstructionList;

public abstract class TacInstruction extends AbstractInstruction{

	private int label;
	
	public TacInstruction (){

	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public abstract boolean replace(Operand find, Operand replacement);

}
