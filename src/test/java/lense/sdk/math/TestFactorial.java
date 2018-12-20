package lense.sdk.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Rational;

public class TestFactorial {

	@Test
	public void testFactorial() {
		
		Natural x = factorial(NativeNumberFactory.newNatural(10));
		assertEquals ( NativeNumberFactory.newNatural("3628800"), x);
		x = factorial(NativeNumberFactory.newNatural(11));
		assertEquals ( NativeNumberFactory.newNatural("39916800"), x);
		x = factorial(NativeNumberFactory.newNatural(15));
		assertEquals ( NativeNumberFactory.newNatural("1307674368000"), x);
		x = factorial(NativeNumberFactory.newNatural(20));
		assertEquals ( NativeNumberFactory.newNatural("2432902008176640000"), x);
		assertEquals ( NativeNumberFactory.newNatural("1124000727777607680000"), factorial(NativeNumberFactory.newNatural(22)));
		assertEquals ( NativeNumberFactory.newNatural("25852016738884976640000"), factorial(NativeNumberFactory.newNatural(23)));
		x = factorial(NativeNumberFactory.newNatural(25));
		assertEquals ( NativeNumberFactory.newNatural("15511210043330985984000000"), x);
	}

	private Natural factorial(Natural value) {
		if (value.isZero() || value.isOne()){
			return NativeNumberFactory.newNatural(1);
		}
		 return value.multiply(factorial(value.predecessor()));
	}

	@Test
	public void testAsRational() {
		
		assertEquals(Rational.constructor(
				lense.core.math.Int32.valueOfNative(3415500),
				lense.core.math.Int32.valueOfNative(10000)
		),
		lense.core.math.BigDecimal.valueOfNative("341.5500").asRational()
		);
		
		
		assertEquals(Rational.constructor(
				lense.core.math.Int32.valueOfNative(1),
				lense.core.math.Int32.valueOfNative(100)
		),
		lense.core.math.BigDecimal.valueOfNative("0.01").asRational()
		);
	}
}
