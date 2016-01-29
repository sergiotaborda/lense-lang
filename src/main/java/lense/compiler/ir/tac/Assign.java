package lense.compiler.ir.tac;

public class Assign  extends AbstractAssignInstruction{

	
	private Operand source;

	public Assign (Operand target, Operand source){
		super(target);
		this.source = source;
	}
	
	public String toString(){
		return super.toString() + source.toString();
	}

	public Operand getSource(){
		return source;
	}

	@Override
	public boolean replace(Operand find, Operand replacement) {
		boolean changed = super.replace(find, replacement);
		
		if (this.source.equals(find)){
			this.source = replacement;
			changed = true;
		}
		return changed;
		
	}

	 
}
