package lense.compiler.ir.stack;


public class PushStringValue extends StackInstruction {


	private String text;

	public PushStringValue (String text){
		this.text = text;
	}
	
	public String toString(){
		return "PUSH \"" + text + "\"";
	}
}
