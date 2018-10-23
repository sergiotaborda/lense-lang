package lense.sdk.math;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import lense.core.math.Natural;
import lense.core.math.Rational;

public class TestFactorial {

	@Test
	public void testFactorial() {
		
		Natural x = factorial(Natural.valueOfNative(10));
		assertEquals ( Natural.valueOf("3628800"), x);
		x = factorial(Natural.valueOfNative(11));
		assertEquals ( Natural.valueOf("39916800"), x);
		x = factorial(Natural.valueOfNative(15));
		assertEquals ( Natural.valueOf("1307674368000"), x);
		x = factorial(Natural.valueOfNative(20));
		assertEquals ( Natural.valueOf("2432902008176640000"), x);
		assertEquals ( Natural.valueOf("1124000727777607680000"), factorial(Natural.valueOfNative(22)));
		assertEquals ( Natural.valueOf("25852016738884976640000"), factorial(Natural.valueOfNative(23)));
		x = factorial(Natural.valueOfNative(25));
		assertEquals ( Natural.valueOf("15511210043330985984000000"), x);
	}

	private Natural factorial(Natural value) {
		if (value.isZero() || value.isOne()){
			return Natural.valueOfNative(1);
		}
		 return value.multiply(factorial(value.predecessor()));
	}

	@Test
	public void testAsRational() {
		
		assertEquals(Rational.constructor(
				lense.core.math.Integer.valueOfNative(3415500),
				lense.core.math.Integer.valueOfNative(10000)
		),
		lense.core.math.BigDecimal.valueOfNative("341.5500").asRational()
		);
		
		
		assertEquals(Rational.constructor(
				lense.core.math.Integer.valueOfNative(1),
				lense.core.math.Integer.valueOfNative(100)
		),
		lense.core.math.BigDecimal.valueOfNative("0.01").asRational()
		);
	}
}
