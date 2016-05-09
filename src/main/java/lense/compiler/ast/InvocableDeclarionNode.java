package lense.compiler.ast;

import java.util.Optional;

import lense.compiler.Visibility;
import lense.compiler.ir.stack.StackInstructionList;
import lense.compiler.ir.tac.TacInstructionList;

public abstract class InvocableDeclarionNode extends AnnotadedLenseAstNode{

	private boolean isAbstract;
	private boolean isNative;
	private Visibility visibility;
	
	public boolean isAbstract() {
		return isAbstract;
	}
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	public boolean isNative() {
		return isNative;
	}
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}
	public Visibility getVisibility() {
		return visibility;
	}
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
	public boolean isStatic() {
		return false;
	}
	public final Optional<StackInstructionList> getStackInstructionsList() {
		return this.getProperty("stackInstructionsList",StackInstructionList.class);
	}

	public void setStackInstructionsList(StackInstructionList stack) {
		setProperty("stackInstructionsList", stack);
	}
	public void setTacInstructionsList(TacInstructionList list) {
		this.setProperty("instructionsList", list);
	}
	
	public Optional<TacInstructionList> getTacInstructionsList() {
		return this.getProperty("instructionsList", TacInstructionList.class);
	}
	
}
