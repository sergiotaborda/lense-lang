package lense.compiler.ir.stack;

import compiler.typesystem.TypeDefinition;

public class AllocateNewObject extends StackInstruction {

	
	private TypeDefinition typeToCreate;

	public AllocateNewObject (TypeDefinition type){
		this.typeToCreate = type;
	}
}
