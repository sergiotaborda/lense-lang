package lense.compiler.ir.stack;


public class DiscardTopStackValue extends StackInstruction {

	private int positionsCount;

	public DiscardTopStackValue ( int positionsCount){
		this.positionsCount = positionsCount;
	}
}
