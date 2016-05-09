package lense.compiler.ir.stack;

public class GoTo extends StackInstruction {

	private int label;

	public GoTo(int label){
		this.label = label;
	}
	
	
	public int getTargetLabel(){
		return label;
	}
	
	public String toString(){
		return "GOTO L" + label;
	}
}
