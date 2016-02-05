package lense.compiler.ir;

import lense.compiler.ir.tac.Operand;
import compiler.typesystem.TypeDefinition;

public class CreateNewInstruction implements Operand {


	private TypeDefinition ownerType;
	private boolean isLiteral;

	public CreateNewInstruction(TypeDefinition ownerType ) {
		this(ownerType,false);
	}
	
	public CreateNewInstruction(TypeDefinition ownerType, boolean isLiteral) {
		this.ownerType =ownerType;
		this.isLiteral = isLiteral;
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

}
