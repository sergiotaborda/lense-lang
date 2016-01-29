package lense.compiler.ir.stack;

import lense.compiler.ir.InstructionType;

public class PushNumberValue extends StackInstruction {

	
	private InstructionType type;
	private Number number;

	public PushNumberValue (Number number , InstructionType type){
		this.type = type;
		this.number = number;
	}
	
	public String toString(){
		return "PUSH " + number + " (" + type + ")";
	}
}
