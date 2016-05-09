package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import lense.compiler.type.TypeDefinition;

public class WriteFieldInstruction implements Operand {

	private String name;
	private TypeDefinition ownerType;
	private boolean isStatic;
	private TypeDefinition fieldType;

	public WriteFieldInstruction(String name, boolean isStatic, TypeDefinition ownerType, TypeDefinition fieldType) {
		this.name = name;
		this.ownerType =ownerType;
		this.isStatic =  isStatic;
		this.fieldType = fieldType;
	}
	
	public String toString(){
		return " set" + (isStatic ? "Static" : "") + "Field " + name + " in " + ownerType;
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

	@Override
	public TypeDefinition getOperandType() {
		return fieldType;
	}
}
