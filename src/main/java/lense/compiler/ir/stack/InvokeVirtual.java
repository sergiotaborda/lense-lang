package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class InvokeVirtual extends AbstractInvokeInstruction {

	public InvokeVirtual (TypeDefinition owner, String name, TypeDefinition returnType){
		super(owner, name, returnType);
	}

	public String toString(){
		return "INVOKE_VIRTUAL " + this.getName();
	}

}
