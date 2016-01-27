package lense.compiler.ir.stack;

public class LoadFromConstantPool extends StackInstruction {

	private int variableIndex;

	public LoadFromConstantPool (int variableIndex){
		this.variableIndex = variableIndex;
	}

}
