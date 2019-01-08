package lense.sdk.math;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

public class TestNatural {

	@Test
	public void test() {
		Natural n = NativeNumberFactory.newNatural(4);
		Natural k = NativeNumberFactory.newNatural(40);

		assertEquals( NativeNumberFactory.newNatural(44),  n.plus(k));
		assertEquals( NativeNumberFactory.newNatural(44),  k.plus(n));

		Natural maxInt = NativeNumberFactory.newNatural(java.lang.Integer.MAX_VALUE);

		assertEquals( NativeNumberFactory.newNatural((long)java.lang.Integer.MAX_VALUE + 4),  n.plus(maxInt));
		assertEquals( NativeNumberFactory.newNatural((long)java.lang.Integer.MAX_VALUE + 4),  maxInt.plus(n));

		Natural maxLong = NativeNumberFactory.newNatural(Long.MAX_VALUE);

		assertEquals( NativeNumberFactory.newNatural(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  n.plus(maxLong));
		assertEquals( NativeNumberFactory.newNatural(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  maxLong.plus(n));
		
		Natural maxULong = NativeNumberFactory.newNatural("18446744073709551615");
		
		Natural maxULongPlus4 = NativeNumberFactory.newNatural("18446744073709551619");
		
		assertEquals( maxULongPlus4,  n.plus(maxULong));
		assertEquals( maxULongPlus4,  maxULong.plus(n));
		
		assertEquals( maxULongPlus4,  maxULong.successor().successor().successor().successor());
	}

	@Test(expected = lense.core.math.ArithmeticException.class)
	public void testNegative() {
		NativeNumberFactory.newNatural(-4);
	}
	
	@Test
	public void testRemainder () {
		
		Natural D = NativeNumberFactory.newNatural(8);
		Natural d = NativeNumberFactory.newNatural(3);
		
		Natural q = D.wholeDivide(d);
		Natural r = D.remainder(d);
		
		assertEquals(D, d.multiply(q).plus(r));
	}
}
