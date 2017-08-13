package lense.sdk.math;

import static org.junit.Assert.*;

import org.junit.Test;

import lense.core.math.Complex;
import lense.core.math.Imaginary;
import lense.core.math.Natural;
import lense.core.math.Real;

public class TestComplex {

	@Test
	public void testComplexAbs () {
		Imaginary img = Imaginary.valueOf(Real.valueOf(Natural.valueOfNative(4)));
		Natural n = Natural.valueOfNative(3);
		Real r = Real.valueOf(n);
		
		Complex c = n.plus(img);
		
		assertEquals("3+4i", c.asString().toString());
		
		assertEquals("9",  r.multiply(r).asString().toString());
		assertEquals("-16",  img.multiply(img).asString().toString());
		
		Real x = Real.valueOf(Natural.valueOfNative(16));
		
		assertEquals("25", r.multiply(r).plus(x).asString().toString());
		assertEquals("25", c.abs().asString().toString());
		
		
	}
}
