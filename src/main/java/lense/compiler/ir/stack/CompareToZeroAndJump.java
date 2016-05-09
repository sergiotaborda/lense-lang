package lense.compiler.ir.stack;

public class CompareToZeroAndJump extends StackInstruction {

	private ComparisonOperation operation;
	private int label;

	public CompareToZeroAndJump(ComparisonOperation operation, int label){
		this.operation = operation;
		this.label = label;
	}
	
	public String toString(){
		return "IF " + operation.name() + " ZERO  JUMP TO L" + label;
	}
	
	public ComparisonOperation getOperation(){
		return operation;
	}

	public int getTargetLabel() {
		return label;
	}
}
