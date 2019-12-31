package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.AnyValue;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Primitives;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

public final class Float64 implements Float, AnyValue {
 
	private static Float64 ZERO = new Float64(0.0d);
	private static Float64 NaN = new Float64(java.lang.Double.NaN);
	private static Float64 NEGATIVE_INFINITY = new Float64(java.lang.Double.NEGATIVE_INFINITY);
	private static Float64 POSITIVE_INIFNITY = new Float64(java.lang.Double.POSITIVE_INFINITY);
	
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
	public boolean equalsTo(Any other) {
	    if (other instanceof Float64) {
			 return java.lang.Double.compare(((Float64)other).value ,this.value) == 0;
		} else if (other instanceof Float64) {
			return java.lang.Double.compare(((Float64)other).value ,this.value) == 0;
		} else if (other instanceof Number && other instanceof Comparable) {
			return BigDecimal.valueOfNative(java.lang.Double.toString(this.value)).equalsTo(other);
		} else {
			return false;
		}
		
	}
	
	@Override
	public HashValue hashValue() {
		return new HashValue(java.lang.Double.hashCode(value));
	}

	@Override
	public Float warpPlus(Float other) {
		return new Float64(this.value + java.lang.Double.parseDouble(other.toString()));
	}

	@Override
	public Float warpMinus(Float other) {
		if (other instanceof Float64){
			return new Float64(this.value - ((Float64)other).value);
		} else if (other instanceof Float32){
			return new Float64(this.value - ((Float32)other).value);
		}  else {
			return new Float64(this.value - java.lang.Double.parseDouble(other.toString()));
		}
	}

	@Override
	public Float wrapMultiply(Float other) {
		if (other instanceof Float64){
			return new Float64(this.value * ((Float64)other).value);
		} else if (other instanceof Float32){
			return new Float64(this.value * ((Float32)other).value);
		}  else {
			return new Float64(this.value * java.lang.Double.parseDouble(other.toString()));
		}
	}

	@Override
	public Float wrapDivide(Float other) {
		if (other instanceof Float64){
			return new Float64(this.value / ((Float64)other).value);
		} else {
			return new Float64(this.value / java.lang.Double.parseDouble(other.toString()));
		}
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
    public Float raiseTo(Float other) {
        if (other instanceof Float32){
            return new Float64(Math.pow(this.value, ((Float32)other).value));
        } else  if (other instanceof Float64){
            return new Float64(Math.pow(this.value, ((Float64)other).value));
        }   else {
            return new Float64(Math.pow(this.value, java.lang.Double.parseDouble(other.toString())));
        }
    }
	
    @Override
    public Integer floor() {
    	// TODO handle infinites and nan
        return Int64.valueOfNative((long)Math.floor(this.value));
    }

    @Override
    public boolean isWhole() {
        return this.value % 1 == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof Float64){
            return Primitives.comparisonFromNative(java.lang.Double.compare(this.value, ((Float64) other).value));
        } else if (other instanceof Number && other instanceof Comparable){
        	return BigDecimal.valueOfNative(java.lang.Double.toString(this.value)).compareWith(other);
        }
        throw new ClassCastException("Cannot compare");
            
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
	public Integer ceil() {
		return BigDecimal.valueOfNative(java.lang.Double.toString(this.value)).ceil();
	}

	private Real promoteNext() {
		return new BigDecimal(new java.math.BigDecimal(this.value));
	}
	
	@Override
	public Float plus(Float other) {
		return warpPlus(other);
	}

	@Override
	public Float minus(Float other) {
		return warpMinus(other);
	}

	@Override
	public Float multiply(Float other) {
		return wrapMultiply(other);
	}

	@Override
	public Float divide(Float other) {
		return wrapDivide(other);
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

	
}
