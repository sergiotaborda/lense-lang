package lense.sdk.math;

import static org.junit.Assert.*;

import org.junit.Test;

import lense.core.math.Complex;
import lense.core.math.Float64;
import lense.core.math.Imaginary;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;
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
		assertEquals("5.0", c.abs().asString().toString());
		
		
	}
	
	   @Test
	    public void testNumberCompare () {
	       
	       assertTrue(NativeNumberFactory.compareNumbers(Rational.one(), Rational.zero()) > 0);
	       
	       Natural64.valueOfNative(5).equalsTo(Float64.valueOfNative(5.0));
	   }
}
