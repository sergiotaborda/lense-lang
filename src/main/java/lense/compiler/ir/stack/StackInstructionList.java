package lense.compiler.ir.stack;

import java.util.ArrayList;
import java.util.List;

public class StackInstructionList {

	private List<StackInstructionListMember> list = new ArrayList<>();
	
	public StackInstructionListMember add(StackInstruction instruction){
		StackInstructionListMember member = new StackInstructionListMember( list.size(), instruction);
		list.add(member);
		return member;
	}
}
