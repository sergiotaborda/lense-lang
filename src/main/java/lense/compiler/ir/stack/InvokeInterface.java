package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class InvokeInterface extends AbstractInvokeInstruction {

	public InvokeInterface(TypeDefinition owner, String name, TypeDefinition returnType) {
		super(owner, name, returnType);
	}

}
