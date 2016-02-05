package lense.compiler.ir.tac;

public class GoTo extends TacInstruction {

	private int targetLabel;

	public GoTo(int targetLabel) {
		this.targetLabel = targetLabel;
	}

	@Override
	public boolean replace(Operand find, Operand replacement) {
		return false;
	}
	
	public String toString(){
		return super.toString() + "GOTO L" + targetLabel;
	}

	public int getTargetLabel() {
		return targetLabel;
	}

	public void setTargetLabel(int target) {
		this.targetLabel = target;
		
	}

}
