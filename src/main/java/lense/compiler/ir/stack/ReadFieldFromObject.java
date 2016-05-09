package lense.compiler.ir.stack;

import lense.compiler.type.TypeDefinition;

public class ReadFieldFromObject extends StackInstruction {

	
	private TypeDefinition returnType;
	private String name;
	private TypeDefinition owner;

	public ReadFieldFromObject (TypeDefinition owner, String name, TypeDefinition returnType){
		this.name = name;
		this.owner= owner;
		this.returnType = returnType;
	}
	public TypeDefinition getOwnerType() {
		return owner;
	}
	
	public TypeDefinition getType() {
		return returnType;
	}
	
	public String getName(){
		return name;
	}
	
	public String toString(){
		return "READ FIELD " + owner.getName() + "." + name + "(" + returnType + ")"; 
	}
}
