package lense.compiler.ir.tac;

public class TemporaryVariable implements Address{

	private int index;

	public TemporaryVariable(int index){
		this.index = index;
	}
	
	public String toString(){
		return "t" + index;
	}
}
