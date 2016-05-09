package lense.compiler.ir.tac;

public class IfJump extends TacInstruction{

	private Operand condition;
	private int targetLabel;
	private boolean conditionValue;

	public IfJump(Operand condition, boolean conditionValue, int label, int targetLabel){
		this.setLabel(label);
		this.targetLabel = targetLabel;
		this.condition = condition; 
		this.conditionValue = conditionValue;
	}
	
	
	
	public Operand getCondition() {
		return condition;
	}



	public int getTargetLabel() {
		return targetLabel;
	}



	public boolean isConditionTrue() { 
		return conditionValue;
	}



	@Override
	public boolean replace(Operand find, Operand replacement) {
		
		if (condition.equals(find)){
			condition = replacement;
		}
		return false;
	}

	public String toString(){
		return super.toString() + "IF " + condition + " IS " + conditionValue + " JUMP TO L" + targetLabel;
	}
}
