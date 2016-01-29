package lense.compiler.ir.stack;

import lense.compiler.ir.Operation;

public class ArithmeticOperate extends StackInstruction{

	
	private Operation operation;

	public ArithmeticOperate (Operation operation){
		this.operation = operation;
	}
	
	public String toString(){
		return operation.name();
	}
}
