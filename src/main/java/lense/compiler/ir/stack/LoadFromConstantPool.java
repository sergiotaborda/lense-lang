package lense.compiler.ir.stack;

public class LoadFromConstantPool extends StackInstruction {

	private int variableIndex;
	private Object value;

	public LoadFromConstantPool (int variableIndex, Object value){
		this.variableIndex = variableIndex;
		this.value = value;
	}

	public int getVariableIndex(){
		return variableIndex;
	}

	public Object getValue() {
		return value;
	}


}
