package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Binary;
import lense.core.lang.Dijunctable;
import lense.core.lang.ExclusiveDijunctable;
import lense.core.lang.HashValue;
import lense.core.lang.Injunctable;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;

public class Int64 extends Integer implements Binary{

	@Constructor(paramsSignature = "")
	public static Int64 constructor (){
		return new Int64(0);
	}
	
	@Constructor(isImplicit = true , paramsSignature = "lense.core.lang.Binary")
	public static Int64 valueOf (Binary binary){
		return new Int64(0);
	}
	
	@PlatformSpecific
	public static Int64 valueOfNative(long n){
		return new Int64(n);
	}  
	
	@PlatformSpecific
	public static Int64 valueOfNative(int n){
		return new Int64(n);
	}  
	
	@Constructor(paramsSignature = "lense.core.math.Natural")
	public static Int64 valueOf(Natural n){
		// TODO validate overflow
		return new Int64(n.toPrimitiveInt());
	}  
	
	@Constructor(paramsSignature = "lense.core.lang.String")
	public static Int64 parse(String s){
		return new Int64(java.lang.Long.parseLong(s));
	}

    long value;

	public Int64(long n) {
		this.value = n;
	}
	
	/**
	 *  Int32 + Int32 = Int32 or ArithmeticException
	 *  ScalableInt32 + Int32 =  ScalableInt32 or bigger
	 *  Int32 + ScalableInt32 = ScalableInt32 or bigger;
	 *  ScalableInt32 + ScalableInt32 = ScalableInt32 or bigger
	 */
	@Override
	public Integer plus(Integer other) {
		if (other instanceof Int32){
			return plus(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return plus( (Int64)other);
		} else {
			return promoteNext().plus(other);
		}
	}
	
	public Int64 plus(Int64 other) {
		return new Int64(Math.addExact(this.value , other.value));
	}
	
	@Override
	public Integer minus(Integer other) {
		if (other instanceof Int32){
			return minus(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return minus( (Int64)other);
		} else {
			return promoteNext().plus(other);
		}
	}
	
	public Int64 minus(Int64 other) {
		return new Int64(Math.subtractExact(this.value , other.value));
	}
	
	@Override
	public Integer multiply(Integer other) {
		if (other instanceof Int32){
			return multiply(new Int64(((Int32)other).value));
		} else 	if (other instanceof Int64){
			return multiply( (Int64)other);
		} else {
			return promoteNext().multiply(other);
		}
	}
	
	public Int64 multiply(Int64 other) {
		return new Int64(Math.multiplyExact(this.value ,other.value));
	}
	
	protected final Integer promoteNext(){
		return new BigInt(BigInteger.valueOf(value));
	}

	@Override
	protected BigInteger asJavaBigInteger() {
		return BigInteger.valueOf(value);
	}

	
	public int compareTo(Integer other ){
		if (other instanceof Int64){
			return Long.compare(this.value,((Int64)other).value );
		} else {
			return asJavaBigInteger().compareTo(other.asJavaBigInteger());
		}
	}


	public final int hashCode(){
		return Long.hashCode(value);
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Long.toString(value)); 
	}

	@Override
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
		    throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("max success reached"));
		}
		return valueOfNative(value + 1);
	}

	@Override
	public boolean isZero() {
		return value == 0;
	}

	@Override
	public boolean isOne() {
		return value == 1;
	}

	@Override
	public final Integer predecessor() {
		if (value == java.lang.Long.MIN_VALUE){
		    throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("min predecessor reached"));
		}
		return valueOfNative(value - 1);
	}
	
	@Override
	public Natural abs() {
		if (this.value < 0){
			return Natural.valueOfNative(-this.value);
		} else {
			return Natural.valueOfNative(this.value);
		}
	}

	@Override
	@Property(name="size")
	public final Natural getSize() {
		return Natural.valueOfNative(64);
	}

	@Override
	public Int64 complement() {
		return new Int64(~value);
	}

	@Override
	public Int64 rightShiftBy(Natural n) {
		return new Int64(value << n.modulus(64));
	}

	@Override
	public Int64 leftShiftBy(Natural n) {
		return new Int64(value >> n.modulus(64));
	}

	@Override
	public Integer symmetric() {
		return new Int64(-value);
	}
	
	public boolean isOdd(){
		return (value & 1) != 0;
	}
	
	public boolean isEven(){
		return (value & 1) == 0;
	}

	@Override
	public boolean getBitAt(Natural index) {
		return rightShiftBy(index).isOdd();
	}
	
	@Override
	public Integer signum() {
		return new Int32( value == 0 ? 0 : (value < 0 ? -1 : 1));
	}

    @Override
    public boolean isNegative() {
        return this.value < 0;
    }
    
    @Override
    public Int64 xor(ExclusiveDijunctable other) {
        if (other instanceof Int64){
            return new Int64(this.value ^ ((Int64)other).value);
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }

    @Override
    public Int64 or(Dijunctable other) {
        if (other instanceof Int64){
            return new Int64(this.value | ((Int64)other).value);
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }

    @Override
    public Int64 and(Injunctable other) {
        if (other instanceof Int64){
            return new Int64(this.value & ((Int64)other).value);
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }
    
    @Override
    public HashValue hashValue() {
        return new HashValue(Long.hashCode(value));
    }
    
    public Integer wholeDivide (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        if (other instanceof Int64){
            return new Int64(this.value / ((Int64)other).value);
        } else if (other instanceof Int32){
            return new Int64(this.value / ((Int32)other).value);
        } else {
            return super.wholeDivide(other);
        }
    }

    public Integer remainder (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        if (other instanceof Int64){
            return new Int64(this.value % ((Int64)other).value);
        } else if (other instanceof Int32){
            return new Int64(this.value % ((Int32)other).value);
        } else {
            return super.wholeDivide(other);
        }
    }
    
   	public Int64 wrapPlus(Int64 other) {
    	return new Int64(this.value + other.value);
   	}


   	public Int64 wrapMultiply(Int64 other) {
    	return new Int64(this.value * other.value);
   	}

   	public Int64 wrapMinus(Int64 other) {
    	return new Int64(this.value - other.value);
   	}


    @Override
    public boolean isPositive() {
       return this.value > 0;
    }

    @Override @PlatformSpecific
    public int toPrimitiveInt() {
        return (int)this.value;
    }
    


}
