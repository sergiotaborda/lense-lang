package lense.sdk.math;

import static org.junit.Assert.*;

import org.junit.Test;

import lense.core.math.Complex;
import lense.core.math.Imaginary;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Rational;
import lense.core.math.Real;

public class TestComplex {

	@Test
	public void testComplexAbs () {
		Imaginary img = Imaginary.valueOf(Rational.valueOf(NativeNumberFactory.newNatural(4)));
		Natural n = NativeNumberFactory.newNatural(3);
		Real r = Rational.valueOf(n);
		
		Complex c = img.plus(n);
		
		assertEquals("3+4i", c.asString().toString());
		
		assertEquals("9",  r.multiply(r).asString().toString());
		assertEquals("-16",  img.multiply(img).asString().toString());
		
		Real x = Rational.valueOf(NativeNumberFactory.newNatural(16));
		
		assertEquals("25", r.multiply(r).plus(x).asString().toString());
		assertEquals("5", c.abs().asString().toString());
		
		
	}
}
