package lense.core.math;

import lense.core.lang.Ordinal;
import lense.core.lang.java.Signature;

@Signature("::lense.core.math.Whole&lense.core.math.SignedNumber&lense.core.math.Comparable<lense.core.math.Integer>")
public interface Integer extends Whole , Comparable, SignedNumber, Ordinal, Progressable {

	public Integer symmetric();

	public Integer plus (Integer other);
	public Integer multiply(Integer other);


	public Integer minus (Natural other);
	
	public Integer plus (Natural other);
	
	public Integer multiply (Natural other);
	
	public Integer minus(Integer other);
	

	public Integer wholeDivide(Integer other);
	
	public Integer wholeDivide(Natural other);

	public Integer remainder (Integer other); 
	
	public Integer successor();
	public Integer predecessor();

	
	public Integer sign();

	public boolean isZero();
    public boolean isOne();

    public boolean isNegative();
    
    public Integer raiseTo(Natural other);

    public Real raiseTo(Real other) ;
    

}
