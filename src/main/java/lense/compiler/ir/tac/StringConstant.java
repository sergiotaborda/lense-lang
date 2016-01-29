package lense.compiler.ir.tac;


public class StringConstant implements Reference {


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
}
