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
import lense.core.lang.reflection.NativeType;
import lense.core.lang.reflection.Type;

@Signature("::lense.core.math.Integer&lense.core.lang.Binary")
@ValueClass
public class Int64 implements Integer , Binary , BigIntegerConvertable , AnyValue{

	public static Int64 ZERO = new Int64(0);
	public static Int64 ONE = new Int64(1);
	public static Int64 MINUS_ONE = new Int64(-1);

	@Constructor(paramsSignature = "")
	public static Int64 constructor (){
		return ZERO;
	}

	@Constructor(isImplicit = true , paramsSignature = "lense.core.lang.Binary")
	public static Int64 valueOf (Binary binary){

		if (binary instanceof Int32 integer) {
			return new Int64(integer.value);
		} else if (binary instanceof Int64 other) {
			return other;
		}
		
		var bitSet = new BitSet(64);
		for (int i = 0; i < 64; i++) {
			if (binary.bitAt(Natural64.valueOfNative(i))) {
				bitSet.set(i);
			}
		}
		
		return new Int64(bitSet.toLongArray()[0]); 
	}

	@PlatformSpecific
	public static Int64 valueOfNative(long n){
		return new Int64(n);
	}  

	@PlatformSpecific
	public static Int64 valueOfNative(int n){
		return new Int64(n);
	}  

	@Constructor(isImplicit = true, paramsSignature = "lense.core.math.Whole")
	public static Int64 valueOf(Whole n){
		if (n instanceof Int64){
			return (Int64)n;
		} else if (n instanceof Int32){
			return valueOfNative(((Int32) n).value);
		} else {
			BigInteger max = BigInteger.valueOf(java.lang.Long.MAX_VALUE);
			BigInteger min = BigInteger.valueOf(java.lang.Long.MIN_VALUE);
			BigInteger val = new BigInteger(n.toString());

			if (val.compareTo(min) >=0 && val.compareTo(max) <=0 ){
				// in range of a int64
				return valueOfNative(val.intValue());
			} else {
				throw ArithmeticException.constructor();
			}
		}

	}  

	@Constructor(paramsSignature = "lense.core.lang.String")
	public static Int64 parse(String s){
		return new Int64(java.lang.Long.parseLong(s));
	}

	long value;

	public Int64(long n) {
		this.value = n;
	}

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

	private Integer plus(Int64 other) {
		try {
			return new Int64(Math.addExact(this.value , other.value));
		} catch (java.lang.ArithmeticException e){
			return this.promoteNext().plus(other);
		}

	}

	@Override
	public Integer minus(Integer other) {
		if (other instanceof Int32 integer){
			return minus(new Int64(integer.value));
		} else 	if (other instanceof Int64 value){
			return minus(value);
		} else {
			return promoteNext().minus(other);
		}
	}

	private Integer minus(Int64 other) {
		try {
			return new Int64(Math.subtractExact(this.value , other.value));
		} catch (java.lang.ArithmeticException e){
			return this.promoteNext().minus(other);
		}
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

	public BigInteger toJavaBigInteger() {
		return BigInteger.valueOf(value);
	}

	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Long.toString(value)); 
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
	public final Integer successor() {
		if (value == java.lang.Integer.MAX_VALUE){
			return this.promoteNext().successor();
		}
		return valueOfNative(value + 1);
	}

	@Override
	public final Integer predecessor() {
		if (value == java.lang.Long.MIN_VALUE){
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
		return Natural64.valueOfNative(64);
	}

	@Override
	public Int64 complement() {
		return new Int64(~value);
	}

	@Override
	public Int64 rightShiftBy(Natural n) {
		return new Int64(value >> NativeNumerics.modulus(n,64));
	}

	@Override
	public Int64 leftShiftBy(Natural n) {
		return new Int64(value << NativeNumerics.modulus(n,64));
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
	public boolean bitAt(Natural index) {
		if (!index.compareWith(Int32.valueOfNative(64)).isSmaller()) {
			return false;
		}
		
		return (value >> NativeNumerics.modulus(index, 64) & 1) == 1;
	}

	@Override
	public boolean isNegative() {
		return this.value < 0;
	}

	@Override
	public Int64 xor(Any other) {
		if (other instanceof Int64){
			return new Int64(this.value ^ ((Int64)other).value);
		} else if (other instanceof Int32){
			return new Int64(this.value ^ ((Int32)other).value);
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 64);
			
			thisBitSet.xor(otherBitSet);
			
			return new Int64(thisBitSet.toLongArray()[0]);
			
		} else {
			throw new IllegalArgumentException("Cannot exclusivly diject with a diferent class");
		}
	}

	@Override
	public Int64 or(Any other) {
		if (other instanceof Int64){
			return new Int64(this.value | ((Int64)other).value);
		} else if (other instanceof Int32){
			return new Int64(this.value | ((Int32)other).value);
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 64);
			
			thisBitSet.or(otherBitSet);
			
			return new Int64(thisBitSet.toLongArray()[0]);
			
		} else {
			throw new IllegalArgumentException("Cannot disject with a diferent class");
		}
	}

	@Override
	public Int64 and(Any other) {
		if (other instanceof Int64){
			return new Int64(this.value & ((Int64)other).value);
		} else if (other instanceof Int32){
			return new Int64(this.value & ((Int32)other).value);
		} else if (other instanceof Binary binary) {
			
			
			var thisBitSet = BitSet.valueOf(new long[] {this.value});
			var otherBitSet = NativeNumerics.bitSetFromBinary(binary, 64);
			
			thisBitSet.and(otherBitSet);
			
			return new Int64(thisBitSet.toLongArray()[0]);

		}else {
			throw new IllegalArgumentException("Cannot inject with a diferent class");
		}
	}

	@Override
	public Integer wholeDivide (Integer other){
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  

		if (other.isOne()) {
			return this;
		} else  if (other instanceof Int64){
			return new Int64(this.value / ((Int64)other).value);
		} else if (other instanceof Int32){
			return new Int64(this.value / ((Int32)other).value);
		} else {
			return this.promoteNext().wholeDivide(other);
		}
	}

	public Integer remainder (Integer other){
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  

		if (other.isOne()) {
			return this;
		} else if (other instanceof Int64){
			return new Int64(this.value % ((Int64)other).value);
		} else if (other instanceof Int32){
			return new Int64(this.value % ((Int32)other).value);
		} else {
			return this.promoteNext().remainder(other).asInteger(); 
		}
	}
	
	@Override
	public Whole modulo(Whole other) {
		if (other.isZero()){
			throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
		}  else if (other.isOne()) {
			return this;
		} 
		
		return this.minus(other.asInteger().multiply(this.divide(other).floor().asInteger()));
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

	@Override
	public Integer sign() {
		if (value == 0) {
			return ZERO;
		} else if (value > 0) {
			return ONE;
		} else {
			return MINUS_ONE;
		}
	}

	public String toString() {
		return String.valueOf(this.value);
	}



	public int hashCode() {
		return Long.hashCode(this.value);
	}

	public boolean equals(Object other) {
		return other instanceof Any && equalsTo((Any)other);
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
	public final HashValue hashValue(){
		return new HashValue(Long.hashCode(this.value));
	}
	
	@Override
	public Progression upTo(Any end) {
		return new NativeOrdinalProgression(this, (Int64)end, true);
	}

	@Override
	public Progression upToExclusive(Any end) {
		return new NativeOrdinalProgression(this, (Int64)end, false);
	}

	@Override
	public Real asReal() {
		return Rational.valueOf(this);
	}

	@Override
	public Integer asInteger() {
		return this;
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
		return Type.forName(this.getClass().getName());
	}

	@Override
	public Whole minus(Whole other) {
		return minus(other.asInteger());
	}

	@Override
	public Integer wholeDivide(Natural other) {
		return this.wholeDivide(other.asInteger());
	}

	@Override
	public Whole plus(Whole other) {
		return plus(other.asInteger());
	}

	@Override
	public Integer raiseTo(Natural other) {
		return this.promoteNext().raiseTo(other);
	}

	@Override
	public @NonNull Real raiseTo(Real other) {
		if ( other.isZero()) {
			return Rational.one();
		} else if (other.isOne()) {
			return this.asReal();
		} else if (other.isWhole()) {
			Integer whole = other.floor();
	
			Rational power = Rational.valueOf(raiseTo(whole.abs()));
			
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
