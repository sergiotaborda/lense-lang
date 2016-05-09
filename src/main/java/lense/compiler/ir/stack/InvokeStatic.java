package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class InvokeStatic extends AbstractInvokeInstruction {

	public InvokeStatic(TypeDefinition owner, String name, TypeDefinition returnType) {
		super(owner, name, returnType);
	}

}
