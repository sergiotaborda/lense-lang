package lense.compiler.ir.tac;

public class Assign  extends AbstractAssignInstruction{

	
	private Reference source;

	public Assign (Reference target, Reference source){
		super(target);
		this.source = source;
	}
	
	public String toString(){
		return super.toString() + source.toString();
	}

	public Reference getSource(){
		return source;
	}

	@Override
	public boolean replace(Reference find, Reference replacement) {
		if (this.source.equals(find)){
			this.source = replacement;
			return true;
		}
		return false;
		
	}

	 
}
