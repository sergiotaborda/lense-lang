package lense.core.math;

import lense.core.lang.java.PlatformSpecific;

@PlatformSpecific
public interface BigIntegerConvertable extends BigDecimalConvertable {
	
	public java.math.BigInteger toJavaBigInteger();

}
