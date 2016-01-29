package lense.compiler.ir.tac;

public class TemporaryVariable implements Reference{

	private int index;

	public TemporaryVariable(int index){
		this.index = index;
	}
	
	public String toString(){
		return "t" + index;
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
}
