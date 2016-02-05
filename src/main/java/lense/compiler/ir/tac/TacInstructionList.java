package lense.compiler.ir.tac;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class TacInstructionList implements Iterable<TacInstruction> {

	LinkedList<TacInstruction> list = new LinkedList<>();

	public void add(TacInstruction instruction){
		list.add(instruction);
	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		for(TacInstruction tac : list){
			builder.append(tac.toString()).append("\n");
		}
		
		return builder.toString();
	}

	public int size() {
		return list.size();
	}

	public TacInstruction get(int i) {
		return list.get(i);
	}

	public void removeAt(int index) {
		list.remove(index);	
	}

	@Override
	public ListIterator<TacInstruction> iterator() {
		return list.listIterator();
	}

	public ListIterator<TacInstruction> endIterator() {
		return list.listIterator(list.size());
	}

	public void set(int index, TacInstruction instruction) {
		list.set(index, instruction);
		
	}


}
