package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.reflection.Type;

public class Exception extends java.lang.RuntimeException implements Any {

	@PlatformSpecific
    private static final long serialVersionUID = 1L;

    @Constructor(paramsSignature = "")
    public static Exception constructor(){
        return new Exception();
    }

	@Constructor(paramsSignature = "lense.core.lang.String, lense.core.lang.Exception")
    public static Exception constructor(String message, Exception cause){
        return new Exception(message, cause);
    }

	@Constructor(paramsSignature = "lense.core.lang.String")
    public static Exception constructor(String message){
        return new Exception(message);
    }

	@Constructor(paramsSignature = "lense.core.lang.Exception")
    public static Exception constructor(Exception cause){
        return new Exception(cause);
    }

	@PlatformSpecific
    public Exception(){
        super();
    }

    public Exception(String message, Exception cause){
        super(message.toString(), cause);
    }
    
    public Exception(String message){
        super(message.toString());
    }
    
    public Exception(Exception cause){
        super(cause);
    }

	
    @Override
	public boolean equalsTo(Any other) {
		return other.getClass().isInstance(this) && this.getClass().isInstance(other);
	}

	@Override
	public HashValue hashValue() {
		return HashValue.constructor();
	}

	@Override
	public String asString() {
		return String.valueOfNative(super.getMessage());
	}

	@Override
	public Type type() {
		return Type.forClass(this.getClass());
	}
    

}
