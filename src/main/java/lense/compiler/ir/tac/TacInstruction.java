package lense.compiler.ir.tac;

import lense.compiler.ir.AbstractInstruction;

public abstract class TacInstruction extends AbstractInstruction{

	private static int nextId = 0;
	
	private int label;
	private int id;
	
	public TacInstruction (){
		this.id = nextId++;
	}

	public boolean equals(Object other){
		return other instanceof TacInstruction && ((TacInstruction)other).id == id;
	}
	
	public int hashCode(){
		return id;
	}
	
	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public abstract boolean replace(Operand find, Operand replacement);

	public String toString(){
		if (label > 0){
			return "L" + label + ": ";
		} else {
			return "";
		}
	}

	public boolean isNop() {
		return false;
	}

	public boolean isLabeled() {
		return label > 0;
	}
}
