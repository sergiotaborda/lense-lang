package lense.core.math;

public class ComparisonException extends lense.core.lang.Exception {

    private static final long serialVersionUID = -501467634001375398L;

    public static ComparisonException constructor (){
        return new ComparisonException(lense.core.lang.String.EMPTY);
    }
    
    public static ComparisonException constructor (lense.core.lang.String message){
        return new ComparisonException(message);
    }
    
    private ComparisonException (lense.core.lang.String message){
        super(message);
    }
    
}
