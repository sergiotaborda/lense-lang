package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import lense.compiler.type.TypeDefinition;

public class CallInstruction implements Operand {

	private String methodName;
	private TypeDefinition ownerType;
	private TypeDefinition returnType;

	public CallInstruction(String name, TypeDefinition ownerType, TypeDefinition returnType) {
		this.methodName = name;
		this.ownerType =ownerType;
		this.returnType = returnType;
	}
	
	public String toString(){
		return " call " + methodName + " in " + ownerType + " returning a " + returnType; 
	}

	public TypeDefinition getOwner(){
		return ownerType;
	}
	
	public TypeDefinition getReturnType(){
		return returnType;
	}
	
	@Override
	public boolean isTemporary() {
		return false;
	}

	public String getName() {
		return methodName;
	}

	@Override
	public boolean isInstruction() {
		return true;
	}

	@Override
	public TypeDefinition getOperandType() {
		return returnType;
	}
	

}
