package lense.compiler.ir.tac;

import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;

public class StringConstant implements Operand {


	private String text;

	public StringConstant(String text){
		this.text = text;
	}
	
	public String toString(){
		return "\"" + text + "\"";
	}
	
	public boolean equals(Object other){
		return other instanceof StringConstant && ((StringConstant)other).text.equals(this.text);
	}
	
	public int hashCode(){
		return text.hashCode();
	}
	
	@Override
	public boolean isTemporary() {
		return false;
	}

	public String getValue() {
		return text;
	}
	
	@Override
	public boolean isInstruction() {
		return false;
	}

	@Override
	public TypeDefinition getOperandType() {
		return LenseTypeSystem.String();
	}
}
