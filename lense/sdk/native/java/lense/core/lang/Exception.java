package lense.core.lang;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Exception extends java.lang.RuntimeException {

    @Native
    private static final long serialVersionUID = 1L;

    @Constructor
    public static Exception constructor(){
        return new Exception();
    }

    @Constructor
    public static Exception constructor(String message, Exception cause){
        return new Exception(message, cause);
    }

    @Constructor
    public static Exception constructor(String message){
        return new Exception(message);
    }

    @Constructor
    public static Exception constructor(Exception cause){
        return new Exception(cause);
    }

    protected Exception(){
        super();
    }

    protected Exception(String message, Exception cause){
        super(message.toString(), cause);
    }
    
    protected Exception(String message){
        super(message.toString());
    }
    
    protected Exception(Exception cause){
        super(cause);
    }
    
//    @Property needs erasure of lense.String? => java.String to work because getMessage has the same name that the user class
//    public String getMessage(){
//        if (super.getMessage() == null){
//            return String.valueOfNative("");
//        } else {
//            return String.valueOfNative(super.getMessage());
//        }
//        
//    }
}
