package lense.compiler.ir.tac;

public class ReturnInstruction extends TacInstruction {

	private Reference right;

	public ReturnInstruction(Reference right){
		this.right = right;
	}
	
	public ReturnInstruction(){
	}
	
	public String toString(){
		return "return " + right.toString();
	}
	
	@Override
	public boolean replace(Reference find, Reference replacement) {
		if (find.equals(right)){
			this.right = replacement;
			return true;
		}
		return false;
	}

}
