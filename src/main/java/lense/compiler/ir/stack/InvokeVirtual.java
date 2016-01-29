package lense.compiler.ir.stack;


public class InvokeVirtual extends AbstractInvokeInstruction {

	private String name;

	public InvokeVirtual(String name) {
		this.name = name;
	}
	
	public String toString(){
		return "INVOKE_VIRTUAL " + name;
	}

}
