package lense.compiler.ir.stack;

import java.util.ArrayList;
import java.util.List;

public class StackInstructionList {

	private List<StackInstruction> list = new ArrayList<>();
	
	public void add(StackInstruction instruction){
		list.add(instruction);
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		for(StackInstruction tac : list){
			builder.append(tac.toString()).append("\n");
		}
		
		return builder.toString();
	}
}
