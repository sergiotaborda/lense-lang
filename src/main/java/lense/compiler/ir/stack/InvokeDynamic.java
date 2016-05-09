package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class InvokeDynamic extends AbstractInvokeInstruction {

	public InvokeDynamic(TypeDefinition owner, String name, TypeDefinition returnType) {
		super(owner, name, returnType);
	}

}
