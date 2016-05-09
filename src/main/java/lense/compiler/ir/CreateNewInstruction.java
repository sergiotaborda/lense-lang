package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import lense.compiler.type.TypeDefinition;

public class CreateNewInstruction implements Operand {


	private TypeDefinition ownerType;
	private boolean isLiteral;
	private String name;

	public CreateNewInstruction(TypeDefinition ownerType , String name) {
		this(ownerType,name, false);
		
	}
	
	public CreateNewInstruction(TypeDefinition ownerType, String name, boolean isLiteral) {
		this.ownerType =ownerType;
		this.isLiteral = isLiteral;
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return " new  " + (isLiteral ? " literal " : "") + ownerType.toString(); 
	}

	@Override
	public boolean isTemporary() {
		return false;
	}

	public TypeDefinition getType() {
		return ownerType;
	}
	
	@Override
	public boolean isInstruction() {
		return true;
	}

	@Override
	public TypeDefinition getOperandType() {
		return ownerType;
	}

}
