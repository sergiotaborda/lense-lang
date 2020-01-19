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
public final class Float32 extends Base implements Float , AnyValue{

	private static Float32 ZERO = new Float32(0.0f);
	private static Float32 NaN = new Float32(java.lang.Float.NaN);
	private static Float32 NEGATIVE_INFINITY = new Float32(java.lang.Float.NEGATIVE_INFINITY);
	private static Float32 POSITIVE_INIFNITY = new Float32(java.lang.Float.POSITIVE_INFINITY);
	
	@Constructor(paramsSignature = "")
	public static Float32 constructor (){
		return ZERO;
	}
	
	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Real")
	public static Float32 valueOf(Real other){
		return new Float32(java.lang.Float.parseFloat(other.toString()));
	}
	
	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Whole")
	public static Float32 valueOf(Whole other){
		return new Float32(java.lang.Float.parseFloat(other.toString()));
	}
	
	public static Float32 valueOfNative(float other){
		return new Float32(other);
	}
	
    float value;
	
	private Float32(float value){
		this.value = value;
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(java.lang.Float.toString(value));
	}
	

	@Override
	public boolean equalsTo(Any other) {
	    if (other instanceof Float32) {
			 return java.lang.Float.compare(((Float32)other).value ,this.value) == 0;
		} else if (other instanceof Float64) {
			return java.lang.Double.compare(((Float64)other).value ,this.value) == 0;
		} else if (other instanceof Number && other instanceof Comparable) {
			return BigDecimal.valueOfNative(java.lang.Float.toString(this.value)).equalsTo(other);
		} else {
			return false;
		}
		
	}
	
	@Override
	public HashValue hashValue() {
		return new HashValue(java.lang.Float.hashCode(value));
	}

	@Override
	public Float plus(Float other) {
		if (other instanceof Float32){
			return new Float32(this.value + ((Float32)other).value);
		} else {
			return new Float64(this.value + ((Float64)other).value);
		} 
	}

	@Override
	public Float minus(Float other) {
		if (other instanceof Float32){
			return new Float32(this.value - ((Float32)other).value);
		} else {
			return new Float64(this.value - ((Float64)other).value);
		} 
	}

	@Override
	public Float multiply(Float other) {
		if (other instanceof Float32){
			return new Float32(this.value * ((Float32)other).value);
		} else {
			return new Float64(this.value * ((Float64)other).value);
		} 
	}

	@Override
	public Float divide(Float other) {
		if (other instanceof Float32){
			return new Float32(this.value / ((Float32)other).value);
		} else {
			return new Float64(this.value / ((Float64)other).value);
		} 
	}
	
	@Override
	public boolean isZero() {
		return java.lang.Float.compare(this.value, 0.0f) == 0;
	}

	@Override
	public boolean isOne() {
		return java.lang.Float.compare(this.value, 1.0f) == 0;
	}

	
	@Override
	public Float symmetric() {
		return new Float32(-this.value);
	}
	
	@Override
	public Integer sign() {
		return new Int32((int)Math.signum(this.value));
	}
	

    @Override
    public Integer floor() {
    	// TODO handle infinites and nan
        return Int32.valueOfNative((int)Math.floor(this.value));
    }

    @Override
    public boolean isWhole() {
        return !this.isInfinity() 
        		&& !this.isNaN() 
        		&& this.value % 1 == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof Float32){
            return Primitives.comparisonFromNative(java.lang.Float.compare(this.value, ((Float32) other).value));
        } else if (other instanceof Number && other instanceof Comparable){
        	return BigDecimal.valueOfNative(java.lang.Float.toString(this.value)).compareWith(other);
        }
        throw new ClassCastException("Cannot compare");
            
    }

    @Override
    public Float abs() {
        return new Float32(Math.abs(this.value));
    }


	@Override
	public boolean isNegative() {
		return java.lang.Float.compare(this.value, 0) < 0;
	}

	@Override
	public boolean isPositive() {
		return java.lang.Float.compare(this.value, 0) > 0;
	}
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> new Type(Float32.class));
	
	@Override
	public Type type() {
		return TYPE_RESOLVER.resolveType();
	}

	@Override
	public Integer ceil() {
		return BigDecimal.valueOfNative(java.lang.Float.toString(this.value)).ceil();
	}

	@Override
	public boolean isNaN() {
		return java.lang.Float.isNaN(this.value);
	}

	@Override
	public boolean isNegativeInfinity() {
		return java.lang.Float.isInfinite(this.value) && java.lang.Float.compare(this.value, 0 ) < 0;
	}

	@Override
	public boolean isPositiveInfinity() {
		return java.lang.Float.isInfinite(this.value) && java.lang.Float.compare(this.value, 0 ) > 0;
	}

	@Override
	public boolean isInfinity() {
		return java.lang.Float.isInfinite(this.value);
	}

    @Override
    public boolean isNegativeZero() {
        return (java.lang.Float.floatToIntBits(this.value) & 0x80000000) < 0;
    }
    
    @Override
    public String toString(){
        return java.lang.Float.toString(value);
    }
    
    @Override
    public int hashCode(){
        return hashValue().hashCode();
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
		return new Float32(1 / this.value);
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


}
