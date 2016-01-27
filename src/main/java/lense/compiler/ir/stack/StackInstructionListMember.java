package lense.compiler.ir.stack;

public class StackInstructionListMember {

	private int listPosition;
	private StackInstruction instruction;

	public StackInstructionListMember(int listPosition, StackInstruction instruction) {
		this.listPosition = listPosition;
		this.instruction= instruction;
	}

}
