package lense.compiler.ir.tac;


public class PrepareParameter extends TacInstruction {

	private Operand right;

	public PrepareParameter(Operand right){
		this.right = right;
	}

	
	public String toString(){
		return super.toString() + "prepare param " + right.toString();
	}
	
	@Override
	public boolean replace(Operand find, Operand replacement) {
		if (find.equals(right)){
			this.right = replacement;
			return true;
		}
		return false;
	}


	public Operand getRight() {
		return right;
	}




}
