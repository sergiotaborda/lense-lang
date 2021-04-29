package lense.core.math;

import java.math.BigInteger;
import java.util.BitSet;

import lense.core.collections.NativeOrdinalProgression;
import lense.core.collections.Progression;
import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.Binary;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.NonNull;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Integer&lense.core.lang.Binary")
@ValueClass
public final class Int32  implements Integer, Binary , BigIntegerConvertable, AnyValue {

	public static Int32 NEGATIVE_ONE = new Int32(-1);
	public static Int32 ZERO = new Int32(0);
	public static Int32 ONE = new Int32(1);
	public static Int32 TWO = new Int32(2);
	public static Int32 THREE = new Int32(3);
	public static Int32 TEN = new Int32(10);
	public static Int32 THIRTY_TWO = new Int32(32);

	@Constructor(paramsSignature = "")
	public static Int32 zero (){
		return ZERO;
	}
	
	@Constructor(paramsSignature = "")
	public static Int32 one (){
		return ONE;
	}

	@Constructor(isImplicit = true, paramsSignature = "lense.core.lang.Binary")
	public static Int32 valueOf (Binary binary){

		if (binary instanceof Int32 integer) {
			return integer;
		} else if (binary instanceof Int64 other) {
			return new Int32((int)other.value);
		}  else if (binary instanceof Whole whole) {
			return valueOf(whole);
		}
		
		var bitSet = new BitSet(32);
		for (int i = 0; i < 32; i++) {
			if (binary.bitAt(Natural64.valueOfNative(i))) {
				bitSet.set(i);
			}
		}
		
		return new Int32((int)bitSet.toLongArray()[0]); 
	}

	public @NonNull Integer raiseTo(int other) {
		if (this.isZero()){
			if (other == 0){
				return Int32.ONE;
			}
			return this;
		} else if (this.isOne()){
			return Int32.ONE;
		} else if (other == 0){
			return Int32.ONE;
		} else if (other == 1){
			return this;
		} else if (other == 2){
			return  this.multiply(this);
		} else if (other == 3){
			return  this.multiply(this).multiply(this);
		}
		return new BigInt(this.toJavaBigInteger()).raiseTo(other);
	}

	@Override
	public BigInteger toJavaBigInteger() {
		return java.math.BigInteger.valueOf(this.value);
	}


	@PlatformSpecific
	public static Int32 valueOfNative(int n){
		switch(n) {
		case 0:
			return ZERO;
		case 1:
			return ONE;
		case 2:
			return TWO;
		case 3:
			return THREE;
		case 10:
			return TEN;
		default:
			return new Int32(n);
		}

	}  

	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Whole")
	public static Int32 valueOf(Whole n){
		if (n instanceof Int32){
			return (Int32)n;
		} else {
			BigInteger max = BigInteger.valueOf(java.lang.Integer.MAX_VALUE);
			BigInteger min = BigInteger.valueOf(java.lang.Integer.MIN_VALUE);
			BigInteger val = new BigInteger(n.toString());

			if (val.compareTo(min) >=0 && val.compareTo(max) <=0 ){
				// in range of a int32
				return valueOfNative(val.intValue());
			} else {
				throw ArithmeticException.constructor();
			}
		}

	}  

	//	@Constructor
	//    public static Int32 parse(lense.core.lang.String s){
	//        return new Int32(java.lang.Integer.parseInt(s.toString()));
	//    }

	int value;

	public Int32(int n) {
		this.value = n;
	}

	@Override
	public Integer plus(Integer other) {
		if (other instanceof Int32){
			return plus(((Int32)other).value);
		} else {
			return promoteNext().plus(other);
		}
	}

	public Integer plus(int value){
		try {
			return new Int32(Math.addExact(this.value , value));
		} catch (java.lang.ArithmeticException e ){
			return promoteNext().plus(new Int32(value));
		}
	}
	@Override
	public Integer minus(Integer other) {

		if (other instanceof Int32) {
			try {
				return new Int32(Math.subtractExact(this.value , ((Int32)other).value));
			} catch (java.lang.ArithmeticException e ){
				return promoteNext().minus(other);
			}
		}
		return promoteNext().minus(other);
	}

	@Override
	public Integer multiply(Integer other) {
		if (other instanceof Int32) {
			try {
				return new Int32(Math.multiplyExact(this.value , ((Int32)other).value));
			} catch (java.lang.ArithmeticException e ){
				return promoteNext().multiply(other);
			}
		}
		return promoteNext().multiply(other);
	}

	protected final Integer  promoteNext(){
		return Int64.valueOfNative(value);
	}

	public final int hashCode(){
		return value;
	}

	public final lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Integer.toString(value)); 
	}

	@Override
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
			return this.promoteNext().successor();
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
		if (value == java.lang.Integer.MIN_VALUE){
			return this.promoteNext().predecessor();
		}
		return valueOfNative(value - 1);
	}

	@Override
	public Natural abs() {
		if (this.value < 0){
			return NativeNumberFactory.newNatural(-this.value);
		} else {
			return NativeNumberFactory.newNatural(this.value);
		}
	}

	@Override
	@Property(name="bitsCount")
	public final Natural getBitsCount() {
		return Natural64.valueOfNative(32);
	}

	@Override
	public Int32 complement() {
		return new Int32(~value);
	}

	@Override
	public Int32 rightShiftBy(Natural n) {
		return new Int32(value >> NativeNumerics.modulus(n, 32));
	}

	@Override
	public Int32 leftShiftBy(Natural n) {
		return new Int32(value << NativeNumerics.modulus(n, 32));
	}

	@Override
	public Integer symmetric() {
		return new Int32(-value);
	}

	public boolean isOdd(){
		return (value & 1) != 0;
	}

	public boolean isEven(){
		return (value & 1) == 0;
	}

	@Override
	public boolean bitAt(Natural index) {
		if (!index.compareWith(Int32.valueOfNative(32)).isSmaller()) {
			return false;
		}
		
		return (value >> NativeNumerics.modulus(index, 32) & 1) == 1;
	}


	@Override
	public boolean isNegative() {
		return this.value < 0;
	}

	@Override
	public Int32 xor(Any other) { // Any is binary
		if (other instanceof Int32){
			return new Int32(this.value ^ ((Int32)other).value);  
		} else if (other instanceof Int64){
			return new Int32((int)(this.value ^ ((Int64)other).value));
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 32);
			
			thisBitSet.xor(otherBitSet);
			
			return new Int32((int)thisBitSet.toLongArray()[0]);
			
		} else {
			throw new IllegalArgumentException("Cannot exclusivly disject with a non Binary class");
		}
	}

	@Override
	public Int32 or(Any other) { // Any is binary
		if (other instanceof Int32){ 
			return new Int32(this.value | ((Int32)other).value);
		} else if (other instanceof Int64){
			return new Int32((int)(this.value | ((Int64)other).value));
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 32);
			
			thisBitSet.or(otherBitSet);
			
			return new Int32((int)thisBitSet.toLongArray()[0]);
			
		} else {
			throw new IllegalArgumentException("Cannot disject with a non Binary class");
		}
	}

	@Override
	public Int32 and(Any other) {
		if (other instanceof Int32){
			return new Int32(this.value & ((Int32)other).value);
		} else if (other instanceof Int64){
			return new Int32((int)(this.value & ((Int64)other).value));
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 32);
			
			thisBitSet.and(otherBitSet);
			
			return new Int32((int)thisBitSet.toLongArray()[0]);
			
		} else {
			throw new IllegalArgumentException("Cannot inject with a non Binary class");
		}
	}



	public Integer wholeDivide (Integer other){
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  

		if (other.isOne()) {
			return this;
		} else if (other instanceof Int32){
			return new Int64(this.value / ((Int32)other).value);
		} else {
			return this.promoteNext().wholeDivide(other);
		}
	}

	

	
	@Override
	public boolean isPositive() {
		return this.value > 0;
	}

	public Int32 wrapPlus(Int32 other) {
		return new Int32(this.value + other.value);
	}

	public Int32 wrapMultiply(Int32 other) {
		return new Int32(this.value * other.value);
	}

	public Int32 wrapMinus(Int32 other) {
		return new Int32(this.value - other.value);
	}

	@Override
	public Integer sign() {
		if (this.value ==0) {
			return Int32.ZERO;
		} else if (this.value > 0) {
			return Int32.ONE;
		} else {
			return Int32.NEGATIVE_ONE;
		}
	}

	public String toString() {
		return String.valueOf(this.value);
	}


	@Override
	public Progression upTo(Any end) {
		return new NativeOrdinalProgression(this, (Int32)end, true);
	}

	@Override
	public Progression upToExclusive(Any end) {
		return new NativeOrdinalProgression(this, (Int32)end, false);
	}



	@Override
	public Float asFloat() {
		return BigFloat.valueOf(this);
	}
	
    @Override
    public Comparison compareWith(Any other) {
    	
    	if (other instanceof Int32) {
			return Primitives.comparisonFromNative(Long.compare(this.value, ((Int32) other).value));
		} else if (other instanceof Int64) {
			return Primitives.comparisonFromNative(Long.compare(this.value, ((Int64) other).value));
		} else if (other instanceof RealLineElement){
        	return NativeNumberFactory.compareNumbers(this, (RealLineElement)other);
        }
        
    	throw new IllegalArgumentException("Cannot compare with " + other.toString());
    }
    
    @Override
    public boolean equalsTo(Any other) {
    	return (other instanceof RealLineElement) && compareWith(other).isEqual();
    }

	@Override
	public HashValue hashValue() {
		return new HashValue(this.value);
	}

	@Override
	public Real asReal() {
		return Rational.valueOf(this);
	}


	@Override
	public Natural gcd(Whole other) {
	   BigInteger gcd =  this.toJavaBigInteger().gcd(new BigInteger(other.toString()));
       
       if (gcd.bitLength() < 63){
           return new Natural64(gcd.longValue());
       } else {
           return new BigNatural(gcd);
       }
	}


	@Override
	public Type type() {
		return Type.fromName(this.getClass().getName());
	}

	@Override
	public Integer asInteger() {
		return this;
	}

	@Override
	public Whole plus(Whole other) {
		return this.plus(other.asInteger());
	}

	@Override
	public Whole minus(Whole other) {
		return this.plus(other.asInteger().symmetric());
	}

	@Override
	public Integer wholeDivide(Natural other) {
		return this.wholeDivide(other.asInteger());
	}


	@Override
	public Integer raiseTo(Natural other) {
		return this.promoteNext().raiseTo(other);
	}

	@Override
	public Real raiseTo(Real other) {
		if ( other.isZero()) {
			return Rational.one();
		} else if (other.isOne()) {
			return this.asReal();
		} else if (other.isWhole()) {
			Integer whole = other.floor();
	
			Rational power = Rational.fraction(raiseTo(whole.abs()), ONE);
			
			if (whole.sign().isNegative()) {
				power = power.invert();
			} 
			
			return power;
		}
		return BigDecimal.constructor(Rational.valueOf(this)).raiseTo(other);
	}

	@Override
	public Complex plus(Imaginary n) {
		return ComplexOverReal.rectangular(this.asReal(), n.real());
	}

	@Override
	public Complex minus(Imaginary n) {
		return ComplexOverReal.rectangular(this.asReal(), n.real().symmetric());
	}

	@Override
	public Imaginary multiply(Imaginary n) {
		return ImaginaryOverReal.valueOf(this.asReal().multiply(n.real()));
	}

	@Override
	public Imaginary divide(Imaginary n) {
		return ImaginaryOverReal.valueOf(this.asReal().divide(n.real()));
	}

	@Override
	public java.math.BigDecimal toBigDecimal() {
		return java.math.BigDecimal.valueOf(value); 
	}

	@Override
	public Integer minus(Natural other) {
		return this.minus(other.asInteger());
	}

	@Override
	public Integer plus(Natural other) {
		return this.plus(other.asInteger());
	}

	@Override
	public Integer multiply(Natural other) {
		return this.multiply(other.asInteger());
	}

	@Override
	public Whole wholeDivide(Whole other) {
		return this.wholeDivide(other.asInteger());
	}

	@Override
	public Whole remainder(Whole other) {
		return this.remainder(other.asInteger());
	}

	public Integer remainder (Integer other){
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  

		if (other.isOne()) {
			return this;
		} else if (other instanceof Int32 integer){
			return new Int32(this.value % integer.value);
		} else {
			return this.promoteNext().remainder(other).asInteger(); 
		}
	}
	
    @Override
    public boolean equals(Object other){
        return other instanceof Any && equalsTo((Any)other);
    }

	@Override
	public Float log() {
		return Float64.valueOfNative(Math.log(this.value));
	}

	
	@Override
	public Rational divide(Integer other) {
		return Rational.fraction(this, other);
	}
	
	@Override
	public Rational divide(Whole other) {
		return Rational.fraction(this, other);
	}




}
