package lense.compiler.ir.tac;

import compiler.typesystem.TypeDefinition;

public class NumericConstant implements Operand {

	
	private TypeDefinition type;
	private Number number;

	public NumericConstant(Number n, TypeDefinition type ){
		this.number = n;
		this.type = type;
	}
	
	public String toString(){
		return number.toString();
	}
	
	public boolean equals(Object other){
		return other instanceof NumericConstant && ((NumericConstant)other).number.equals(this.number);
	}
	
	public int hashCode(){
		return number.hashCode();
	}
	
	@Override
	public boolean isTemporary() {
		return false;
	}

	public TypeDefinition getType() {
		return type;
	}

	public Number getValue() {
		return number;
	}
	@Override
	public boolean isInstruction() {
		return false;
	}
}
