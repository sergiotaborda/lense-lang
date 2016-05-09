package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import lense.compiler.type.TypeDefinition;

public class ReadFieldInstruction implements Operand {

	private String name;
	private TypeDefinition ownerType;
	private TypeDefinition returnType;
	private boolean isStatic;

	public ReadFieldInstruction(String name, boolean isStatic, TypeDefinition ownerType, TypeDefinition returnType) {
		this.name = name;
		this.ownerType =ownerType;
		this.returnType = returnType;
		this.isStatic =  isStatic;
	}
	
	public String toString(){
		return " set" + (isStatic ? "Static" : "") + "Field " + name + " in " + ownerType + " returning a " + returnType; 
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean isInstruction() {
		return true;
	}

	public TypeDefinition getOwnerType() {
		return ownerType;
	}

	public TypeDefinition getReturnType() {
		return returnType;
	}

	@Override
	public TypeDefinition getOperandType() {
		return returnType;
	}

	
}
