package lense.compiler.ir.stack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StackInstructionList implements Iterable<StackInstruction>{

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

	@Override
	public Iterator<StackInstruction> iterator() {
		return list.iterator();
	}

	public ListIterator<StackInstruction> startIterator() {
		return list.listIterator();
	}

	public int size() {
		return list.size();
	}
}
