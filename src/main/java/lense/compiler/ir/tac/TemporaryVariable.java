package lense.compiler.ir.tac;

public class TemporaryVariable implements Operand{

	private int index;

	public TemporaryVariable(int index){
		this.index = index;
	}
	
	public String toString(){
		return "t" + index;
	}
	
	public String getName(){
		return "<" + index;
	}
	
	public boolean equals(Object other){
		return other instanceof TemporaryVariable && ((TemporaryVariable)other).index == this.index;
	}
	
	public int hashCode(){
		return index;
	}
	
	@Override
	public boolean isTemporary() {
		return true;
	}
	
	@Override
	public boolean isInstruction() {
		return false;
	}
}
