package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class AllocateNewObject extends StackInstruction {

	
	private InstructionType typeToCreate;

	public AllocateNewObject (InstructionType type){
		this.typeToCreate = type;
	}
}
