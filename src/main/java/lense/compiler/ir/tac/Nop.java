package lense.compiler.ir.tac;

public class Nop extends TacInstruction {

	public Nop(int label) {
		this.setLabel(label);
	}

	@Override
	public boolean replace(Operand find, Operand replacement) {
		return false;
	}
	
	public String toString(){
		return super.toString() + "NOP";
	}
	
	public boolean isNop() {
		return true;
	}
}
