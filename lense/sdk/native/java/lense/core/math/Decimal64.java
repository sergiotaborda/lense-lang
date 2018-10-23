package lense.core.math;


import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.java.Constructor;

public final class Decimal64 extends Decimal{

	@Constructor(paramsSignature = "")
	public static Decimal64 constructor (){
		return new Decimal64(0d);
	}
	
	
	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Real")
	public static Decimal64 valueOf(Real other){
		if (other instanceof Decimal64){
			return (Decimal64)other;
		} else {
			return new Decimal64(other.promoteToBigDecimal().value.toString());
		}
	}
	
	@Constructor(isImplicit= true, paramsSignature = "lense.core.math.Whole")
	public static Decimal64 valueOf(Whole other){
		return new Decimal64(other.asJavaBigInteger().toString());
	}
	
	private double value;
	
	Decimal64(String value){
        this(Double.parseDouble(value));
    }

    Decimal64(double value){
		this.value = value;
	}
	
	public lense.core.lang.String asString(){
		return lense.core.lang.String.valueOfNative(Double.toString(value));
	}
	
	
	@Override
	public boolean equalsTo(Any other) {
	    if (other instanceof Decimal64) {
			 return Double.compare(((Decimal64)other).value ,this.value) == 0;
		}
		return super.equalsTo(other);
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(Double.hashCode(value));
	}
	
	@Override
	public boolean isZero() {
		return Double.compare(this.value, 0) == 0;
	}

	@Override
	public boolean isOne() {
		return Double.compare(this.value, 1) == 0;
	}

	@Override
	public Real plus(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value + ((Decimal64)other).value);
		} else {
			return promoteNext().plus(other);
		}
	}


	@Override
	public Real minus(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value - ((Decimal64)other).value);
		} else {
			return promoteNext().minus(other);
		}
	}

	@Override
	public Real multiply(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value * ((Decimal64)other).value);
		} else {
			return promoteNext().multiply(other);
		}
	}
	
	@Override
	protected BigDecimal promoteToBigDecimal() {   
        return new BigDecimal(java.math.BigDecimal.valueOf(value));
    }

	@Override
	public Real divide(Real other) {
		if (other instanceof Decimal64){
			return new Decimal64(this.value / ((Decimal64)other).value);
		} else {
			return promoteNext().divide(other);
		}
	}

	@Override
	public Real symmetric() {
		return new Decimal64(-this.value);
	}

	@Override
	public Integer signum() {
		return new Int32((int)Math.signum(this.value));
	}


    @Override
    protected Real promoteNext() {
        return this.promoteToBigDecimal();
    }
    
    public Real raiseTo(Real other) {
        if (other instanceof Decimal64){
            return new Decimal64(Math.pow(this.value, ((Decimal64)other).value));
        } else if (other instanceof Decimal32){
            return new Decimal64(Math.pow(this.value, ((Decimal32)other).value));
        } else {
            return new Decimal64(Math.pow(this.value, valueOf(other).value));
        }
    }


    @Override
    public Integer asInteger() {
        return Integer.valueOfNative((long)this.value);
    }

    

    @Override
    public boolean isWhole() {
        return this.value % 1 == 0;
    }


    @Override
    public Comparison compareWith(Any other) {
        if (other instanceof Decimal64){
            return Comparison.valueOfNative(Double.compare(this.value, ((Decimal64) other).value));
        } else if (other instanceof Real){
            return super.compareWith((Real)other);
        }
        throw new ClassCastException("Cannot compare");
    }
    
    @Override
    public Real abs() {
        return new Decimal64(Math.abs(this.value));
    }
}
