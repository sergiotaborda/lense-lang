package lense.compiler.ir.tac;

import lense.compiler.ir.InstructionType;

public class LocalVariable implements Operand {

	private String name;
	private InstructionType type;

	public LocalVariable (String name, InstructionType type){
		this.name = name;
		this.type = type;
	}
	
	public String toString(){
		return name;
	}
	
	public String getName(){
		return name;
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
}
