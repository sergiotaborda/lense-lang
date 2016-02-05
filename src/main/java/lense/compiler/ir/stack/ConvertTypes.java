package lense.compiler.ir.stack;

import compiler.typesystem.TypeDefinition;

public class ConvertTypes extends StackInstruction {

	
	private TypeDefinition original;
	private TypeDefinition target;

	public ConvertTypes(TypeDefinition original, TypeDefinition target ){
		this.original = original;
		this.target = target;
	}
}
