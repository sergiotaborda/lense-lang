package lense.compiler.ir.stack;

import compiler.typesystem.TypeDefinition;

public class CompareToZeroAndJump extends StackInstruction {

	private ComparisonOperation operation;

	public CompareToZeroAndJump(ComparisonOperation operation, TypeDefinition type){
		this.operation = operation;
	}
}
