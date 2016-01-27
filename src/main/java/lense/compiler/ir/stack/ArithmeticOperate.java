package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class ArithmeticOperate extends StackInstruction{

	
	private Operation operation;
	private InstructionType type;

	public ArithmeticOperate (Operation operation, InstructionType type){
		this.type = type;
		this.operation = operation;
	}
}
