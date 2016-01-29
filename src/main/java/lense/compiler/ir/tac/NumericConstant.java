package lense.compiler.ir.tac;

import lense.compiler.ir.InstructionType;

public class NumericConstant implements Reference {

	
	private InstructionType type;
	private Number number;

	public NumericConstant(Number n, InstructionType type ){
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
}
