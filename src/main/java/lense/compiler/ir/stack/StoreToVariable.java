package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class StoreToVariable extends StackInstruction {

	
	private int variableIndex;
	private TypeDefinition type;

	public StoreToVariable (int variableIndex, TypeDefinition type){
		if (type == null){
			throw new IllegalArgumentException();
		}
		this.variableIndex = variableIndex;
		this.type = type;
	}
	
	public TypeDefinition getType(){
		return type;
	}
	
	public String toString(){
		return "STORE " + variableIndex + "(" +  type.getName() + ")";
	}

	public int getVariableIndex() {
		return variableIndex;
	}
}
