package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class PushConstantValue extends StackInstruction {

	
	private InstructionType type;
	private Number number;

	public PushConstantValue (Number number , InstructionType type){
		this.type = type;
		this.number = number;
	}
}
