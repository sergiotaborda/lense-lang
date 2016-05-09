package lense.compiler.ir.tac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class TacInstructionListOptimizer {


	public TacInstructionList optimize(TacInstructionList list) {
		if (list.size() <= 1){
			return list;
		}
		boolean changed = true;
		while (changed){
			changed = false;

			changed = optimizeCopyPropagation(list);
			changed = changed | optimizeRemoveNop(list);
			changed = changed | optimizeRemoveUnnecessaryGoto(list);
			// TODO GOTO after return is unncessary
		}

		return list;
	}


	private boolean optimizeRemoveUnnecessaryGoto(TacInstructionList list) {

		ListIterator<TacInstruction> it = list.endIterator();
		Map<Integer, Integer> redirection = new HashMap<>();

		boolean changed = false;

		while (it.hasPrevious()){
			TacInstruction current = it.previous();
			if (current instanceof GoTo){ 
				GoTo c = ((GoTo)current);

				if (redirection.keySet().contains(c.getTargetLabel())){
					changed = true;
					c.setTargetLabel(redirection.get(c.getTargetLabel()));
				}

			} else if (current.isLabeled()){ // current is labeled but is not a GOTO
				TacInstruction previous = it.previous();
				if (previous instanceof GoTo){
					GoTo c= (GoTo)previous;
					if (c.getTargetLabel() == current.getLabel()){
						// unnecessary goto
						changed = true;
						it.remove();

						if (c.getLabel() > 0){
							redirection.put(c.getLabel(), current.getLabel());
						}


					} else if (redirection.keySet().contains(c.getTargetLabel())){
						changed = true;
						c.setTargetLabel(redirection.get(c.getTargetLabel()));
					}
				}
			}
		}

		return changed;
	}


	private boolean optimizeRemoveNop(TacInstructionList list) {
		ListIterator<TacInstruction> it = list.iterator();


		Set<TacInstruction> toRemove = new HashSet<> ();

		// passe the first one
		it.next();

		while (it.hasNext()){

			if (it.hasPrevious()){
				TacInstruction previous = it.previous();
				it.next();
				TacInstruction next = it.next();

				if (previous.isNop()){
					next.setLabel(previous.getLabel());
					toRemove.add(previous);
				}
			}
		}

		if (!toRemove.isEmpty()){
			it = list.iterator();
			while (it.hasNext()){
				if(toRemove.contains(it.next())){
					it.remove();
				}
			}
			return true;
		} else {
			return false;
		}
	}


	private boolean optimizeCopyPropagation(TacInstructionList list) {
		boolean changed = false;
		for (int i =0; i < list.size()- 1; i++){
			TacInstruction instruction = list.get(i);
			if (instruction instanceof Assign){
				Assign assign = (Assign)instruction;
				if (assign.getTarget().isTemporary() && !(assign.getSource().isInstruction())){
					if (assign.isLabeled()){
						list.set(i, new Nop(assign.getLabel()));
					} else {
						list.removeAt(i);
					}
					for (int j =i; j< list.size(); j++){
						changed = list.get(j).replace(assign.getTarget(), assign.getSource());
					}
				}

			} else  if (instruction instanceof AssignAfterBinaryOperation){
				AssignAfterBinaryOperation assignBinary = (AssignAfterBinaryOperation)instruction;

				if (assignBinary.getTarget().isTemporary()){
					for (int j =i+1; j< list.size(); j++){
						TacInstruction next = list.get(j);
						if (next instanceof Assign){
							Assign assign = (Assign)next;
							if (assignBinary.getTarget().equals(assign.getSource())){
								assignBinary.replace(assignBinary.getTarget(), assign.getTarget());
								if (next.isLabeled()){
									list.set(j, new Nop(assign.getLabel()));
								} else {
									list.removeAt(j);
								}

								j--;
								changed = true;
								break;
							}
						}

					}
				}
			}
		}

		return changed;
	}

}
