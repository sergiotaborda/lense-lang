package lense.compiler.ir.stack;

public class LoadFromVariable extends StackInstruction {

	private int variableIndex;

	public LoadFromVariable (int variableIndex){
		this.variableIndex = variableIndex;
	}
	
	
	public String toString(){
		return "LOAD " + variableIndex;
	}
}
