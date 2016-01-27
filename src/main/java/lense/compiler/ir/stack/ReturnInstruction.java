package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class ReturnInstruction extends StackInstruction {

	
	private InstructionType type;

	public ReturnInstruction (InstructionType type){
		this.type = type;
	}
}
