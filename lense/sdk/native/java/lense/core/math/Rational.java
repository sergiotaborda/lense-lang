package lense.core.math;

import lense.core.lang.java.Constructor;
import lense.core.lang.java.NotReplacedPlaceholderException;

public interface Rational extends Real {

	@Constructor(paramsSignature = "")
	public static Rational one(){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "")
	public static Rational zero(){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Whole, lense.core.math.Whole")
	public static Rational fraction(Whole numerator, Whole denominator){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Whole", isImplicit = true)
	public static Rational valueOf(Whole other){
		throw new NotReplacedPlaceholderException();
	}
	
	@Constructor(paramsSignature = "lense.core.math.Integer", isImplicit = true)
	public static Rational valueOf(Integer other){
		throw new NotReplacedPlaceholderException();
	}
	
	
	@lense.core.lang.java.Property( name = "numerator")
	@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.math.Integer" , paramsSignature = "")
	Integer getNumerator();

	@lense.core.lang.java.Property( name = "denominator")
	@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.math.Natural" , paramsSignature = "")
	Natural getDenominator();

	Rational invert();

}
