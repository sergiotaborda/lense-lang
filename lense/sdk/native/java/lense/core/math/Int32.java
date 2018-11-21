package lense.core.math;

import java.math.BigInteger;

import lense.core.lang.Any;
import lense.core.lang.Binary;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;

public final class Int32 extends Integer implements Binary  {

    @Constructor(paramsSignature = "")
    public static Int32 constructor (){
        return new Int32(0);
    }

    @Constructor(isImplicit = true, paramsSignature = "lense.core.lang.Binary")
    public static Int32 valueOf (Binary binary){
        return new Int32(0);
    }

    @PlatformSpecific
    public static Int32 valueOfNative(int n){
        return new Int32(n);
    }  

    @Constructor(isImplicit = true, paramsSignature = "lense.core.math.Whole")
    public static Int32 valueOf(Whole n){
        if (n instanceof Int32){
            return (Int32)n;
        } else {
            BigInteger max = BigInteger.valueOf(java.lang.Integer.MAX_VALUE);
            BigInteger min = BigInteger.valueOf(java.lang.Integer.MIN_VALUE);
            BigInteger val = n.asJavaBigInteger();

            if (val.compareTo(min) >=0 && val.compareTo(max) <=0 ){
                // in range of a int32
                return new Int32(val.intValue());
            } else {
                throw ArithmeticException.constructor();
            }
        }

    }  

//    @Constructor(paramsSignature = "lense.core.math.Natural")
//    public static Int32 valueOf(Natural n){
//        BigInteger max = BigInteger.valueOf(java.lang.Integer.MAX_VALUE);
//        BigInteger min = BigInteger.valueOf(java.lang.Integer.MIN_VALUE);
//        BigInteger val = n.asJavaBigInteger();
//
//        if (val.compareTo(min) >=0 && val.compareTo(max) <=0 ){
//            // in range of a int32
//            return new Int32(val.intValue());
//        } else {
//            throw ArithmeticException.constructor();
//        }
//    }  

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
        } catch (ClassCastException e ){
            return promoteNext().plus(new Int32(value));
        }
    }
    @Override
    public Integer minus(Integer other) {
        try {
            return new Int32(Math.subtractExact(this.value , ((Int32)other).value));
        } catch (ClassCastException e ){
            return promoteNext().plus(other);
        }
    }

    @Override
    public Integer multiply(Integer other) {
        try {
            return new Int32(Math.multiplyExact(this.value , ((Int32)other).value));
        } catch (ClassCastException e ){
            return promoteNext().multiply(other);
        }
    }

    protected final Integer  promoteNext(){
        return Int64.valueOfNative(value);
    }

    @Override
    protected BigInteger asJavaBigInteger() {
        return BigInteger.valueOf(value);
    }

    public int compareTo(Integer other ){
        if (other instanceof Int32){
            return  java.lang.Integer.compare(this.value, ((Int32)other).value);
        } else {
            return asJavaBigInteger().compareTo(other.asJavaBigInteger());
        }
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
        if (value == java.lang.Integer.MIN_VALUE){
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
    public final Natural bitsCount() {
        return Natural.valueOfNative(32);
    }

    @Override
    public Int32 complement() {
        return new Int32(~value);
    }

    @Override
    public Int32 rightShiftBy(Natural n) {
        return new Int32(value << n.modulus(32));
    }

    @Override
    public Int32 leftShiftBy(Natural n) {
        return new Int32(value >> n.modulus(32));
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
    public Int32 xor(Any other) { // Any is binary
        if (other instanceof Int32){
            return new Int32(this.value ^ ((Int32)other).value);  
        } else if (other instanceof Int64){
            return new Int32((int)(this.value ^ ((Int64)other).value));
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }

    @Override
    public Int32 or(Any other) { // Any is binary
        if (other instanceof Int32){ 
            return new Int32(this.value | ((Int32)other).value);
        } else if (other instanceof Int64){
            return new Int32((int)(this.value | ((Int64)other).value));
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }

    @Override
    public Int32 and(Any other) {
        if (other instanceof Int32){
            return new Int32(this.value & ((Int32)other).value);
        } else if (other instanceof Int64){
            return new Int32((int)(this.value & ((Int64)other).value));
        } else {
            throw new IllegalArgumentException("Cannot inject with a diferent class");
        }
    }

    @Override
    public HashValue hashValue() {
        return new HashValue(this.value);
    }

    public Integer wholeDivide (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        if (other instanceof Int32){
            return new Int32(this.value / ((Int32)other).value);
        } else {
            return super.wholeDivide(other);
        }
    }

    public Integer remainder (Integer other){
        if (other.isZero()){
            throw ArithmeticException.constructor(lense.core.lang.String.valueOfNative("Cannot divide by zero"));
        }  
        if (other instanceof Int32){
            return new Int32(this.value % ((Int32)other).value);
        } else {
            return super.wholeDivide(other);
        }
    }

    @Override
    public boolean isPositive() {
        return this.value > 0;
    }

    @Override
    public int toPrimitiveInt() {
        return value;
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





}
