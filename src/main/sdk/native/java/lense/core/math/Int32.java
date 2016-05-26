package lense.core.math;

public class Int32 extends Integer{

	
	public static Int32 valueOf(Natural n){
		// TODO validate overflow
		return new Int32(n.toPrimitiveInt());
	}  
	
	public static Int32 parse(String s){
		return new Int32(java.lang.Integer.parseInt(s));
	}

	private int value;  

	private Int32(int value){
		this.value = value;
	}
}
