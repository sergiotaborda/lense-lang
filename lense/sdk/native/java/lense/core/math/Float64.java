package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Primitives;
import lense.core.lang.java.ValueClass;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

@ValueClass
public final class Float64 extends Base implements Float, AnyValue {

	private static Float64 ZERO = new Float64(0.0d);
	static Float64 NaN = new Float64(java.lang.Double.NaN);
	static Float64 NEGATIVE_INFINITY = new Float64(java.lang.Double.NEGATIVE_INFINITY);
	static Float64 POSITIVE_INIFNITY = new Float64(java.lang.Double.POSITIVE_INFINITY);

	@Constructor(paramsSignature = "")
	public static Float64 constructor (){
		return ZERO;
	}

	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Real")
	public static Float64 valueOf(Real other){
		if (other instanceof Rational) {
			return new Float64(java.lang.Double.parseDouble(BigDecimal.constructor((Rational)other).toString()));
		}
		return new Float64(java.lang.Double.parseDouble(other.toString()));
	}

	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Whole")
	public static Float64 valueOf(Whole other){ // TODO overload
		return new Float64(java.lang.Double.parseDouble(other.toString()));
	}

	public static Float64 valueOfNative(double other){
		return new Float64(other);
	}

	double value;

	Float64(double value){
		this.value = value;
	}

	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Double.toString(value));
	}


	@Override
	public Float asFloat() {
		return this;
	}
	
    @Override
    public Comparison compareWith(Any other) {
    	
    	if (other instanceof Float32) {
    		return Primitives.comparisonFromNative(java.lang.Double.compare(this.value, ((Float32) other).value));
    	} else if (other instanceof Float64) {
    		return Primitives.comparisonFromNative(java.lang.Double.compare(this.value, ((Float64) other).value));
    	} else if (other instanceof RealLineElement) {
    		return NativeNumberFactory.compareFloat(this, ((RealLineElement)other).asFloat());
    	}
        
    	throw new IllegalArgumentException("Cannot compare with " + other.toString());
    }
    
    @Override
    public boolean equalsTo(Any other) {
    	return (other instanceof RealLineElement) && this.compareWith(other).isEqual();
    }

	@Override
	public HashValue hashValue() {
		return new HashValue(java.lang.Double.hashCode(value));
	}

	@Override
	public Float plus(Float other) {
		if (other instanceof Float32){
			return new Float64(this.value + ((Float32)other).value);
		} else if (other instanceof Float64){
			return new Float64(this.value + ((Float64)other).value);
		}
		return BigFloat.valueOf(this).plus(other);
	}

	@Override
	public Float minus(Float other) {
		if (other instanceof Float32){
			return new Float64(this.value - ((Float32)other).value);
		} else  if (other instanceof Float64){
			return new Float64(this.value - ((Float64)other).value);
		} 
		return BigFloat.valueOf(this).minus(other);
	}

	@Override
	public Float multiply(Float other) {
		if (other instanceof Float32){
			return new Float64(this.value * ((Float32)other).value);
		} else  if (other instanceof Float64){
			return new Float64(this.value * ((Float64)other).value);
		} 
		return BigFloat.valueOf(this).multiply(other);
	}

	@Override
	public Float divide(Float other) {
		if (other instanceof Float32){
			return new Float64(this.value / ((Float32)other).value);
		} else if (other instanceof Float64){
			return new Float64(this.value / ((Float64)other).value);
		} 
		return BigFloat.valueOf(this).divide(other);
	}

	@Override
	public boolean isZero() {
		return java.lang.Double.compare(this.value, 0d) == 0;
	}

	@Override
	public boolean isOne() {
		return java.lang.Double.compare(this.value, 1.0d) == 0;
	}


	@Override
	public Float symmetric() {
		return new Float64(-this.value);
	}

	@Override
	public Integer sign() {
		return new Int32((int)Math.signum(this.value));
	}



	@Override
	public Float floor() {
    	if (this.isNaN() || this.isInfinity() || this.isNegativeZero()) {
    		return this;
    	}
        return Float64.valueOfNative(Math.floor(this.value));
	}
	
	@Override
	public Float ceil() {
    	if (this.isNaN() || this.isInfinity() || this.isNegativeZero()) {
    		return this;
    	}
        return Float64.valueOfNative(Math.ceil(this.value));
	}


	@Override
	public Float round() {
		return NativeNumerics.round(this);
	}
	
	@Override
	public boolean isWhole() {
		return this.value % 1 == 0;
	}


	@Override
	public Float abs() {
		return new Float64(Math.abs(this.value));
	}


	@Override
	public boolean isNegative() {
		return java.lang.Double.compare(this.value, 0) < 0;
	}

	@Override
	public boolean isPositive() {
		return java.lang.Double.compare(this.value, 0) > 0;
	}
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> new Type(Float64.class));

	@Override
	public Type type() {
		return TYPE_RESOLVER.resolveType();
	}


	@Override
	public boolean isNaN() {
		return java.lang.Double.isNaN(this.value);
	}

	@Override
	public boolean isNegativeInfinity() {
		return java.lang.Double.isInfinite(this.value) && java.lang.Double.compare(this.value, 0 ) < 0;
	}

	@Override
	public boolean isPositiveInfinity() {
		return java.lang.Double.isInfinite(this.value) && java.lang.Double.compare(this.value, 0 ) > 0;
	}

	@Override
	public boolean isInfinity() {
		return java.lang.Double.isInfinite(this.value) ;
	}


	public boolean isNegativeZero() {
        return java.lang.Double.isInfinite(1 / this.value) && Math.copySign (1.0, this.value) < 0d;
	}

	@Override
	public Float log() {
		return new Float64(Math.log(this.value));
	}


	@Override
	public Float exp() {
		return new Float64(Math.exp(this.value));
	}

	@Override
	public Float invert() {
		return new Float64(1 / this.value);
	}

	@Override
	public Float raiseTo(Float other) {
		if (other instanceof Float32){
			return new Float64(Math.pow(this.value, ((Float32)other).value));
		} else if (other instanceof Float64){
			return new Float64(Math.pow(this.value, ((Float64)other).value));
		} 

		return BigFloat.valueOf(this).raiseTo(other);
	}

	@Override
	public Float raiseTo(Whole other) {
		return BigFloat.valueOf(this).raiseTo(other);
	}
	
	@Override
	public Float remainder(Float other) {
		return NativeNumerics.remainder(this, other);
	}

	@Override
	public Float modulo(Float other) {
		return NativeNumerics.modulo(this, other);
	}
}
