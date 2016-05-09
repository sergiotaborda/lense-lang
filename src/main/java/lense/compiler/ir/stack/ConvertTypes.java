package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class ConvertTypes extends StackInstruction {

	
	private TypeDefinition original;
	private TypeDefinition target;

	public ConvertTypes(TypeDefinition original, TypeDefinition target ){
		this.original = original;
		this.target = target;
	}

	public TypeDefinition getOriginal() {
		return original;
	}

	public TypeDefinition getTarget() {
		return target;
	}
	
	
}
