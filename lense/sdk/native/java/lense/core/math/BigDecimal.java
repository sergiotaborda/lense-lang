package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.String;

public final class BigDecimal extends Decimal {

    @Constructor(paramsSignature = "")
    public static BigDecimal constructor (){
        return new BigDecimal(java.math.BigDecimal.ZERO);
    }

    @Constructor(paramsSignature = "lense.core.math.Rational")
    public static BigDecimal constructor (Rational other){
     
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
    	} else if (other instanceof Whole) {
    		return new java.math.BigDecimal(((Whole)other).asJavaBigInteger()).compareTo(this.value) == 0;
    	} else if (other instanceof Real) {
    		return equalsTo(((Real)other).promoteToBigDecimal());
    	}
        return false;
    }

    @Override
    public HashValue hashValue() {
        return new HashValue(this.value.hashCode());
    }

    @Override
    public Real plus(Real other) {
        return new BigDecimal(this.value.add(other.promoteToBigDecimal().value));
    }

    @Override
    public Real minus(Real other) {
        return new BigDecimal(this.value.subtract(other.promoteToBigDecimal().value));
    }

    @Override
    public Real multiply(Real other) {
        return new BigDecimal(this.value.multiply(other.promoteToBigDecimal().value));
    }

    @Override
    public Real divide(Real other) {
        return new BigDecimal(this.value.divide(other.promoteToBigDecimal().value));
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
    protected Real promoteNext() {
        return this;
    }

    @Override
    public Real symmetric() {
        return new BigDecimal(this.value.negate());
    }

    @Override
    public Integer signum() {
        return new Int32(this.value.signum());
    }


    @Override
    protected BigDecimal promoteToBigDecimal() {
        return this;
    }

    @Override
    public Real raiseTo(Real other) {
        // TODO use bigdecimal arithmetic. use double for now
        return Decimal64.valueOf(this).raiseTo(other);
    }
    
    @Override
    public Integer asInteger() {
        return  Integer.valueOfNative(this.value.toBigInteger());
    }


    @Override
    public boolean isWhole() {
        return this.value.remainder(java.math.BigDecimal.ONE).compareTo(java.math.BigDecimal.ZERO) == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof BigDecimal){
            return Comparison.valueOfNative(this.value.compareTo(((BigDecimal) other).value));
        } else if (other instanceof Real){
            return super.compareWith((Real)other);
        }
        throw new ClassCastException("Cannot compare");
            
    }

    @Override
    public Real abs() {
       return new BigDecimal(this.value.abs());
    }

	
    @Override
	public Rational asRational() {
		 java.lang.String full = this.value.toPlainString();
		 int pos = full.indexOf(".");
		 if (pos < 0) {
			 // no decimal part
			 return Rational.constructor(Integer.valueOfNative(full), Integer.ONE);
		 } else {
			 full = full.replace(".", "");
			 int i =0;
			 while(full.charAt(i) == '0') {
				 i++;
			 }
			 return Rational.constructor(Integer.valueOfNative(full.substring(i)), Integer.valueOfNative(10).raiseTo(Natural.valueOfNative(full.length() - pos)));
		 }
	}



}
