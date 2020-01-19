package lense.core.math;

public interface SignedNumber extends Number {

    public abstract boolean isZero();
    public abstract boolean isNegative();
    public abstract boolean isPositive();
    
    public abstract Integer sign();
}
