package lense.sdk.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import lense.core.math.Complex;
import lense.core.math.Imaginary;
import lense.core.math.ImaginaryOverReal;
import lense.core.math.Int32;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;
import lense.core.math.Rational;
import lense.core.math.Real;

public class TestComplex {

	@Test
	public void testComplexAbs () {
		Imaginary img = ImaginaryOverReal.valueOf(Rational.valueOf(NativeNumberFactory.newNatural(4)));
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
	       
	    assertTrue(NativeNumberFactory.compareNumbers(Rational.one(), Rational.zero()).isGreater());
	       
	    Real dist = dist(Int32.valueOfNative(3),Int32.valueOfNative(4));
	    
	    assertTrue(Natural64.valueOfNative(5).asReal().equalsTo(dist));
	    assertTrue( dist.equalsTo( Natural64.valueOfNative(5).asReal()));
	}
	
	private Real dist(lense.core.math.Integer a, lense.core.math.Integer b){
	    return a.raiseTo(Natural64.valueOfNative(2)).plus(b.raiseTo(Natural64.valueOfNative(2))).raiseTo(Rational.fraction(Int32.ONE, Int32.TWO));
	}
}
