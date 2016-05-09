package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public abstract class AbstractInvokeInstruction extends StackInstruction {

	private TypeDefinition returnType;
	private String name;
	private TypeDefinition owner;

	public AbstractInvokeInstruction (TypeDefinition owner, String name, TypeDefinition returnType){
		this.name = name;
		this.owner= owner;
		this.returnType = returnType;
	}
	public final TypeDefinition getOwnerType() {
		return owner;
	}
	
	public final TypeDefinition getType() {
		return returnType;
	}
	
	public final String getName(){
		return name;
	}
	
}
