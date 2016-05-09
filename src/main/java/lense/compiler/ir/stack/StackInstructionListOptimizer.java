package lense.compiler.ir.stack;

import java.util.ListIterator;

public class StackInstructionListOptimizer {
	
	public StackInstructionList optimize(StackInstructionList stack) {
		if (stack.size() <= 1){
			return stack;
		}
		boolean changed = true;
		while (changed){
			changed = false;

			changed = optimizeStoreFollowedByLoad(stack);

		}

		return stack;
	}
	
	private boolean optimizeStoreFollowedByLoad(StackInstructionList stack) {
		ListIterator<StackInstruction> it = stack.startIterator();
		boolean changed = false;
		while(it.hasNext()){
			StackInstruction current = it.next();
			if (current instanceof StoreToVariable) {
				int index = ((StoreToVariable)current).getVariableIndex();
				if (!it.hasNext()){
					break;
				}
				StackInstruction next = it.next();
				if (next instanceof LoadFromVariable && ((LoadFromVariable)next).getVariableIndex() == index){
					it.remove();
					it.previous();
					it.remove();
					changed = true;
				}
			}
	
		}
		return changed;
	}

}
