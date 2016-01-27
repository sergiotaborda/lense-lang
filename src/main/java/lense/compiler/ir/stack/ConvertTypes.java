package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class ConvertTypes extends StackInstruction {

	
	private InstructionType original;
	private InstructionType target;

	public ConvertTypes(InstructionType original, InstructionType target ){
		this.original = original;
		this.target = target;
	}
}
