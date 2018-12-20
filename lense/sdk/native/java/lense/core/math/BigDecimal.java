package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.ValueClass;

@ValueClass
public final class BigDecimal implements Decimal {

	
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
    	
        java.math.BigDecimal n = new java.math.BigDecimal( other.getNumerator().asJavaBigInteger());
        java.math.BigDecimal d = new java.math.BigDecimal( other.getDenominator().asJavaBigInteger());
        
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
        return new BigDecimal(this.value.divide(new java.math.BigDecimal(other.toString())));
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
        return Float64.valueOf(this).raiseTo(Float64.valueOf(other)).asDecimal();
    }
    
    @Override
    public Integer floor() {
        return new BigInt(this.value.toBigInteger());
    }

	@Override
	public Integer ceil() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public boolean isWhole() {
        return this.value.remainder(java.math.BigDecimal.ONE).compareTo(java.math.BigDecimal.ZERO) == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof BigDecimal){
            return Comparison.valueOfNative(this.value.compareTo(((BigDecimal) other).value));
        } else  if (other instanceof Real){
        	Real real = (Real) other;
        	if (real.isNaN() || real.isNegativeInfinity()) {
        		return Comparison.valueOfNative(1);
        	} else if (real.isPositiveInfinity()) {
        		return Comparison.valueOfNative(-1);
        	}
        	
            return compareWith(real.asDecimal());
        } else if (other instanceof Number  && other instanceof Comparable){
        	if (this.toString().equals(other.toString())){
				 return Comparison.valueOfNative(0);
			 }
        	return Comparison.valueOfNative(this.value.compareTo(new java.math.BigDecimal(other.toString())));
        }
        
        throw new ClassCastException("Cannot compare " + this.getClass().getName() + " to " + other.getClass().getName());
            
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
			 return Rational.constructor( new BigInt( new java.math.BigInteger(full)));
		 } else {
			 full = full.replace(".", "");
			 int i =0;
			 while(full.charAt(i) == '0') {
				 i++;
			 }
			 return Rational.constructor(new BigInt( new java.math.BigInteger(full.substring(i))), Int32.TEN.raiseTo(full.length() - pos));
		 }
	}

	public java.lang.String toString() {
		return value.toString();
	}

	@Override
	public Decimal asDecimal() {
		return this;
	}

	
	@Override
	public boolean isNaN() {
		return false;
	}

	@Override
	public boolean isNegativeInfinity() {
		return false;
	}

	@Override
	public boolean isPositiveInfinity() {
		return false;
	}

	@Override
	public boolean isInfinity() {
		return false;
	}


}
