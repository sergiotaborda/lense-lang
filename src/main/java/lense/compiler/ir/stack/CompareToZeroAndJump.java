package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class CompareToZeroAndJump extends StackInstruction {

	private ComparisonOperation operation;

	public CompareToZeroAndJump(ComparisonOperation operation, InstructionType type){
		this.operation = operation;
	}
}
