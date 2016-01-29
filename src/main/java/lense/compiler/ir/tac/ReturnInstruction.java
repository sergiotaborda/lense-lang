package lense.compiler.ir.tac;


public class ReturnInstruction extends TacInstruction {

	private Operand right;

	public ReturnInstruction(Operand right){
		this.right = right;
	}
	
	public ReturnInstruction(){
	}
	
	public String toString(){
		return "return " + right.toString();
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
