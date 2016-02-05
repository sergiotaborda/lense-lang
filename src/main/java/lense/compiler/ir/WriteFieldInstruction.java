package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import compiler.typesystem.TypeDefinition;

public class WriteFieldInstruction implements Operand {

	private String name;
	private TypeDefinition ownerType;
	private boolean isStatic;

	public WriteFieldInstruction(String name, boolean isStatic, TypeDefinition ownerType) {
		this.name = name;
		this.ownerType =ownerType;
		this.isStatic =  isStatic;
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
}
