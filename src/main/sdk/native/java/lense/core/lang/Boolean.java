package lense.core.lang;

public class Boolean implements Any{

	public static Boolean TRUE = new Boolean(true);
	public static Boolean FALSE = new Boolean(false);
	
	private boolean value;

	private Boolean(boolean value){
		this.value = value;
	}
	
	public boolean toPrimitiveBoolean() {
		return value;
	}
	
	
	public Boolean negate(){
		return this.value ? FALSE : TRUE;
	}
	
	public Boolean flipAll(){
		return this.value ? FALSE : TRUE;
	}

}
