package lense.compiler.ir.tac;

import lense.compiler.ir.AbstractInstruction;

public class TacInstruction extends AbstractInstruction{

	private static int nextId = 0;
	
	private Address myAddress;
	
	
	public TacInstruction (){
		myAddress = new TemporaryVariable(++nextId);
	}
}
