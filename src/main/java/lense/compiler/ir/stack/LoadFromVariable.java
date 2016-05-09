package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class LoadFromVariable extends StackInstruction {

	private int variableIndex;
	private TypeDefinition type;

	public LoadFromVariable (int variableIndex, TypeDefinition type){
		this.variableIndex = variableIndex;
		this.type = type;
	}
	
	public int getVariableIndex(){
		return variableIndex;
	}
	
	public String toString(){
		return "LOAD " + variableIndex + "(" +  type.getName() + ")";
	}
}
