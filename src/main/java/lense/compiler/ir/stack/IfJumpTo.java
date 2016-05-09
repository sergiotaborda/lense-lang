package lense.compiler.ir.stack;

public class IfJumpTo extends StackInstruction {

	private int label;
	private boolean logicValue;
	
	public IfJumpTo(boolean logicValue, int label){
		this.label = label;
		this.logicValue = logicValue;
	}
	
	public int getTargetLabel(){
		return label;
	}
	
	public boolean getLogicValue(){
		return logicValue;
	}
	
	public String toString(){
		return "IF "  +  logicValue + " JUMP TO L" + label;
	}
}
