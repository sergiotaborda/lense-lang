package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;

public final class Float64 implements Float{

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
		return new Float64(java.lang.Double.parseDouble(other.asDecimal().toString()));
	}
	
	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Whole")
	public static Float64 valueOf(Whole other){ // TODO overload
		return new Float64(java.lang.Double.parseDouble(other.toString()));
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
	public Float wrapPlus(Float other) {
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
        return Int64.valueOfNative((long)this.value);
    }


    @Override
    public boolean isWhole() {
        return this.value % 1 == 0;
    }

    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof Float64){
            return Comparison.valueOfNative(java.lang.Double.compare(this.value, ((Float64) other).value));
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


	@Override
	public Decimal asDecimal() {
		return new BigDecimal(new java.math.BigDecimal(this.value));
	}
	
	private Real promoteNext() {
		return new BigDecimal(new java.math.BigDecimal(this.value));
	}
	
	@Override
	public Real plus(Real other) {
		if (this.isNaN()) {
			return this;
		} else if (other.isNaN()) {
			return other;
		} else if ((this.isNegativeInfinity() && other.isPositiveInfinity()) || (other.isNegativeInfinity() && this.isPositiveInfinity())) {
			return NaN;
		} else if (this.isInfinity()) {
			return this;
		}  else if (other.isInfinity()) {
			return other;
		} else if (other instanceof Float32){
			double val = this.value + ((Float32)other).value;
			
			if (java.lang.Double.isInfinite(val) || java.lang.Double.isNaN(val)) {
				return promoteNext().plus(other);
			} else {
				return new Float64(val);
			}
		} else if (other instanceof Float64){
			double val = this.value + ((Float64)other).value;
			
			if (java.lang.Double.isInfinite(val) || java.lang.Double.isNaN(val)) {
				return promoteNext().plus(other);
			} else {
				return new Float64(val);
			}
		} else {
			return promoteNext().plus(other);
		}
	}

	@Override
	public Real minus(Real other) {
		if (this.isNaN()) {
			return this;
		} else if (other.isNaN()) {
			return other;
		} else if ((this.isPositiveInfinity() && other.isPositiveInfinity()) || (other.isNegativeInfinity() && this.isNegativeInfinity())) {
			return NaN;
		} else if (this.isInfinity()) {
			return this;
		}  else if (other.isInfinity()) {
			return other;
		} else if (other instanceof Float32){
			double val = this.value + ((Float32)other).value;
			
			if (java.lang.Double.isInfinite(val) || java.lang.Double.isNaN(val)) {
				return promoteNext().plus(other);
			} else {
				return new Float64(val);
			}
		} else if (other instanceof Float64){
			double val = this.value + ((Float64)other).value;
			
			if (java.lang.Double.isInfinite(val) || java.lang.Double.isNaN(val)) {
				return promoteNext().plus(other);
			} else {
				return new Float64(val);
			}
		} else {
			return promoteNext().plus(other);
		}
	}

	@Override
	public Real multiply(Real other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Real divide(Real other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Real raiseTo(Real other) {
		// TODO Auto-generated method stub
		return null;
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
