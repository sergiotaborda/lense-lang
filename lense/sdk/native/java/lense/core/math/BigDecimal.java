package lense.core.math;

import java.math.RoundingMode;

import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@ValueClass
public final class BigDecimal implements Real  , AnyValue {

	
	public static BigDecimal ZERO = new BigDecimal(java.math.BigDecimal.ZERO);
	
    @Constructor(paramsSignature = "")
    public static BigDecimal constructor (){
        return ZERO;
    }

    @Constructor(paramsSignature = "lense.core.math.Rational")
    public static BigDecimal constructor (Rational other){
     
    	if (other.isZero()) {
    		return ZERO;
    	} else if (other.isWhole()) {
    		return valueOfNative(other.getNumerator().toString());
    	}
    	
        java.math.BigDecimal n = new java.math.BigDecimal( other.getNumerator().toString());
        java.math.BigDecimal d = new java.math.BigDecimal( other.getDenominator().toString());
        
        return new BigDecimal(n.divide(d));
    }

    
  
    public static BigDecimal valueOfNative (java.lang.String value){
        return new BigDecimal(new java.math.BigDecimal(value));
    }
     
    final java.math.BigDecimal value;

    BigDecimal(java.math.BigInteger value){
        this(new java.math.BigDecimal(value.toString()));
    }

    BigDecimal(java.math.BigDecimal value){
        this.value = value;
    }

    public String asString(){
        return String.valueOfNative(value.toString());
    }


    @Override
    public boolean equalsTo(Any other) {
    	if (other instanceof BigDecimal) {
    		return ((BigDecimal)other).value.compareTo(this.value) == 0;
    	} else if (other instanceof Number  && other instanceof Comparable) {
    		return new java.math.BigDecimal(other.toString()).compareTo(this.value) == 0;
    	}
    	
        return false;
    }

    @Override
    public HashValue hashValue() {
        return new HashValue(this.value.hashCode());
    }

    @Override
    public Real plus(Real other) {
        return new BigDecimal(this.value.add(new java.math.BigDecimal(other.toString())));
    }

    @Override
    public Real minus(Real other) {
        return new BigDecimal(this.value.subtract(new java.math.BigDecimal(other.toString())));
    }

    @Override
    public Real multiply(Real other) {
        return new BigDecimal(this.value.multiply(new java.math.BigDecimal(other.toString())));
    }

    @Override
    public Real divide(Real other) {
    	if (this.isWhole() && other.isWhole()) {
    		return this.asInteger().divide(other.asInteger());
    	}
    	
    	java.math.BigDecimal devisor = new java.math.BigDecimal(other.toString());
    	try {
    		return new BigDecimal(this.value.divide(devisor));
    	} catch (java.lang.ArithmeticException e) {
    		return new BigDecimal(this.value.divide(devisor, RoundingMode.HALF_EVEN));
    	}
    }

    @Override
    public boolean isZero() {
        return this.value.signum() == 0;
    }

    @Override
    public boolean isOne() {
        return this.value.equals(java.math.BigDecimal.ONE);
    }

    @Override
    public Real symmetric() {
        return new BigDecimal(this.value.negate());
    }

    @Override
    public Integer sign() {
        return new Int32(this.value.signum());
    }


    @Override
    public Real raiseTo(Real other) {
        // TODO use bigdecimal arithmetic. use double for now
        return valueOfNative( Float64.valueOf(this).raiseTo(Float64.valueOf(other)).asString().toString());
    }
    
    @Override
    public Integer floor() {
        return new BigInt(this.value.toBigInteger());
    }

	@Override
	public Integer ceil() {
	   throw new UnsupportedOperationException("Ceil not implmented yet in BigDecimal");
	}

    @Override
    public boolean isWhole() {
        return this.value.remainder(java.math.BigDecimal.ONE).compareTo(java.math.BigDecimal.ZERO) == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
    	if (other == null) {
    		throw new IllegalArgumentException("Cannot compare with null");
    	}
    	
    	if (!(other instanceof Number) && !(other instanceof Comparable)) {
    		throw new IllegalArgumentException("Can only compare with other comparable number");
    	}
    	
        if (other instanceof BigDecimal){
            return Primitives.comparisonFromNative(this.value.compareTo(((BigDecimal) other).value));
        } else {
        	return Primitives.comparisonFromNative(NativeNumberFactory.compareNumbers(this, (Number)other));
        }
   
    }

    @Override
    public Real abs() {
       return new BigDecimal(this.value.abs());
    }

	public Rational asRational() {
		 java.lang.String full = this.value.toPlainString();
		 int pos = full.indexOf(".");
		 if (pos < 0) {
			 // no decimal part
			 return BigRational.valueOf( new BigInt( new java.math.BigInteger(full)));
		 } else {
			 full = full.replace(".", "");
			 int i =0;
			 while(full.charAt(i) == '0') {
				 i++;
			 }
			 return BigRational.constructor(new BigInt( new java.math.BigInteger(full.substring(i))), Int32.TEN.raiseTo(full.length() - pos));
		 }
	}

	public java.lang.String toString() {
		return value.toString();
	}

	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}

	@Override
	public Integer asInteger() {
		return floor();
	}

	@Override
	public boolean isNegative() {
		return this.value.signum() < 0;
	}

	@Override
	public boolean isPositive() {
		return this.value.signum() > 0;
	}

	@Override
	public Complex plus(Imaginary other) {
		return Complex.retangular(this, other.real());
	}

	@Override
	public Complex minus(Imaginary other) {
		return Complex.retangular(this, other.real().symmetric());
	}
	

	@Override
	public Imaginary multiply(Imaginary other) {
		return Imaginary.valueOf(this.multiply(other.real()));
	}
	

	@Override
	public Imaginary divide(Imaginary other) {
		return Imaginary.valueOf(this.divide(other.real()));
	}

}
