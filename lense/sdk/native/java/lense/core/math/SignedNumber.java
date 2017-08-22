package lense.core.math;

public interface SignedNumber  {

    public abstract boolean isZero();
    public abstract boolean isNegative();
    public abstract boolean isPositive();
    
    public abstract Integer signum();
}
