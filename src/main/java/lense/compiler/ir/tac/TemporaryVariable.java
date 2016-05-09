package lense.compiler.ir.tac;

import lense.compiler.type.TypeDefinition;

public class TemporaryVariable implements Operand{

	private int index;
	private TypeDefinition type;

	public TemporaryVariable(int index, TypeDefinition type){
		if (type == null){
			throw new IllegalArgumentException();
		}
		this.index = index;
		this.type = type;
	}
	
	public String toString(){
		return "t" + index;
	}
	
	public String getName(){
		return "<" + index;
	}
	
	public TypeDefinition getType(){
		return type;
	}
	
	public boolean equals(Object other){
		return other instanceof TemporaryVariable && ((TemporaryVariable)other).index == this.index;
	}
	
	public int hashCode(){
		return index;
	}
	
	@Override
	public boolean isTemporary() {
		return true;
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
