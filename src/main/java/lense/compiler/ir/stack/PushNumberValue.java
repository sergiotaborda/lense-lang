package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class PushNumberValue extends StackInstruction {

	
	private TypeDefinition type;
	private Number number;

	public PushNumberValue (Number number , TypeDefinition type){
		this.type = type;
		this.number = number;
	}
	
	public String toString(){
		return "PUSH " + number + " (" + type.getName() + ")";
	}
}
