package lense.compiler.ir.java;

import lense.compiler.ir.InstructionType;
import lense.compiler.ir.tac.Operand;

public class CallInstruction implements Operand {

	private String methodName;
	private InstructionType ownerType;
	private InstructionType returnType;

	public CallInstruction(String name, InstructionType ownerType, InstructionType returnType) {
		this.methodName = name;
		this.ownerType =ownerType;
		this.returnType = returnType;
	}
	
	public String toString(){
		return " call " + methodName; 
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	public String getName() {
		return methodName;
	}

}
