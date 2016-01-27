package lense.compiler.ir.stack;

public class StoreToVariable extends StackInstruction {

	
	private int variableIndex;

	public StoreToVariable (int variableIndex){
		this.variableIndex = variableIndex;
	}
}
