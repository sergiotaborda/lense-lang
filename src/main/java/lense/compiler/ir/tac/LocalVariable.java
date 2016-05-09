package lense.compiler.ir.tac;

import lense.compiler.type.TypeDefinition;

public class LocalVariable implements Operand {

	private String name;
	private TypeDefinition type;

	public LocalVariable (String name, TypeDefinition type){
		if (type == null){
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.type = type;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
	}
	
	public TypeDefinition getType(){
		return type;
	}
	
	public boolean equals(Object other){
		return other instanceof LocalVariable && ((LocalVariable)other).name.equals(this.name);
	}
	
	public int hashCode(){
		return name.hashCode();
	}

	@Override
	public boolean isTemporary() {
		return false;
	}
	
	@Override
	public boolean isInstruction() {
		return false;
	}

	@Override
	public TypeDefinition getOperandType() {
		return type;
	}
}
