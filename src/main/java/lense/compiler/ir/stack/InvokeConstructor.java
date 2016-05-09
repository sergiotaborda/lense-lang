package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class InvokeConstructor extends AbstractInvokeInstruction {

	public InvokeConstructor(TypeDefinition owner, String constructorName) {
		super(owner, constructorName, owner);
	}

	public String toString(){
		return "NEW " + this.getOwnerType().getName() + (this.getName() == null ? "" : "." + this.getName());
	}
}
