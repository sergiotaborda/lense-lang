package lense.compiler.ir.tac;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TacInstructionList implements Iterable<TacInstruction> {

	List<TacInstruction> list = new ArrayList<>();
	
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
	public Iterator<TacInstruction> iterator() {
		return list.iterator();
	}
}
