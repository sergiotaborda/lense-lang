package lense.compiler.ir.stack;

public class Label extends StackInstruction {

	private int label;

	public Label(int label) {
		this.label = label;
	}
	
	public int getLabel(){
		return label;
	}
	
	public String toString(){
		return "L" + label + ":";
	}

}
