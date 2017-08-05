package lense.core.math;

public class ArithmeticException extends lense.core.lang.Exception {

    private static final long serialVersionUID = -501467634001375398L;

    public static ArithmeticException constructor (lense.core.lang.String message){
        return new ArithmeticException(message);
    }
    
    private ArithmeticException (lense.core.lang.String message){
        super(message);
    }
    
}
