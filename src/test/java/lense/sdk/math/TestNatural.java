package lense.sdk.math;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import lense.core.math.Natural;

public class TestNatural {

	@Test
	public void test() {
		Natural n = Natural.valueOfNative(4);
		Natural k = Natural.valueOfNative(40);

		assertEquals( Natural.valueOfNative(44),  n.plus(k));
		assertEquals( Natural.valueOfNative(44),  k.plus(n));

		Natural maxInt = Natural.valueOfNative(java.lang.Integer.MAX_VALUE);

		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE + 4),  n.plus(maxInt));
		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE + 4),  maxInt.plus(n));

		Natural maxLong = Natural.valueOfNative(Long.MAX_VALUE);

		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  n.plus(maxLong));
		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  maxLong.plus(n));
		
		Natural maxULong = Natural.valueOf("18446744073709551615");
		
		Natural maxULongPlus4 = Natural.valueOf("18446744073709551619");
		
		assertEquals( maxULongPlus4,  n.plus(maxULong));
		assertEquals( maxULongPlus4,  maxULong.plus(n));
		
		assertEquals( maxULongPlus4,  maxULong.successor().successor().successor().successor());
	}

	@Test(expected = ArithmeticException.class)
	public void testNegative() {
		Natural.valueOfNative(-4);
	}
	

}
