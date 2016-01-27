package lense.compiler.ir;

public class InstructionType {

	public static final InstructionType INT = new InstructionType("int");
	public static final InstructionType BOOLEAN = new InstructionType("boolean");
	public static final InstructionType String = new InstructionType("string");
	
	private String typeName;

	public InstructionType(String typeName){
		this.typeName = typeName;
	}
	
	public boolean equals(Object other){
		return other instanceof InstructionType && ((InstructionType)other).typeName.equals(this.typeName);
	}
	
	public int hashCode(){
		return typeName.hashCode();
	}
}
