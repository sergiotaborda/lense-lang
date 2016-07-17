package lense.sdk.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import lense.core.math.BigInt;
import lense.core.math.Int32;
import lense.core.math.Int64;
import lense.core.math.Integer;
import lense.core.math.Natural;
import lense.core.math.ScalableInt32;
import lense.core.math.ScalableInt64;
import lense.core.math.Whole;

public class TestWhole {

	@Test
	public void testPositive() {
		Whole n = Natural.valueOfNative(4);
		Whole k = Natural.valueOfNative(40);

		assertEquals( Natural.valueOfNative(44),  n.plus(k));
		assertEquals( Natural.valueOfNative(44),  k.plus(n));

		Whole maxInt = Natural.valueOfNative(java.lang.Integer.MAX_VALUE);

		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE + 4),  n.plus(maxInt));
		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE + 4),  maxInt.plus(n));

		Whole maxLong = Natural.valueOfNative(Long.MAX_VALUE);

		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  n.plus(maxLong));
		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(4))),  maxLong.plus(n));
		
		Whole maxULong = Natural.valueOf("18446744073709551615");
		
		Whole maxULongPlus4 = Natural.valueOf("18446744073709551619");
		
		assertEquals( maxULongPlus4,  n.plus(maxULong));
		assertEquals( maxULongPlus4,  maxULong.plus(n));
		
		assertEquals( maxULongPlus4,  maxULong.successor().successor().successor().successor());
	}

	@Test
	public void testInteger() {
		Whole n = Integer.valueOfNative(-4);
		Whole k = Natural.valueOfNative(40);

		assertEquals( Natural.valueOfNative(36),  n.plus(k));
		assertEquals( Natural.valueOfNative(36),  k.plus(n));

		Whole maxInt = Natural.valueOfNative(java.lang.Integer.MAX_VALUE);

		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE - 4),  n.plus(maxInt));
		assertEquals( Natural.valueOfNative((long)java.lang.Integer.MAX_VALUE - 4),  maxInt.plus(n));

		Whole maxLong = Natural.valueOfNative(Long.MAX_VALUE);

		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(4))),  n.plus(maxLong));
		assertEquals( Natural.valueOf(BigInteger.valueOf(Long.MAX_VALUE).subtract(BigInteger.valueOf(4))),  maxLong.plus(n));
		
		Whole maxULong = Natural.valueOf("18446744073709551615");
		
		Whole maxULongPlus4 = Natural.valueOf("18446744073709551611");
		
		assertEquals( maxULongPlus4,  n.plus(maxULong));
		assertEquals( maxULongPlus4,  maxULong.plus(n));
		
		assertEquals( maxULongPlus4,  maxULong.predecessor().predecessor().predecessor().predecessor());
	
	}
	
	@Test(expected = ArithmeticException.class)
	public void testIn32Limit(){
		Whole k = Int32.valueOfNative(1);
		Whole m = Int32.valueOfNative(java.lang.Integer.MAX_VALUE);
		
		m.plus(k);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testInt64UpperLimit(){
		Whole k = Int64.valueOfNative(2);
		Whole m = Int64.valueOfNative(java.lang.Long.MAX_VALUE - 1);
		
		m.plus(k);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testInt64LowerLimit(){
		Whole k = Int64.valueOfNative(-2);
		Whole m = Int64.valueOfNative(java.lang.Long.MIN_VALUE + 1);
		
		m.plus(k);
	}
	
	@Test
	public void testIn64PlusInt32(){
		Whole k = Int32.valueOfNative(2);
		Whole m = Int64.valueOfNative(1);
		
		Whole r = m.plus(k);
		
		assertEquals(Int64.valueOfNative(3), r);
		
		assertTrue(r instanceof Int64);
	}
	
	@Test
	public void testIn64MinusInt32(){
		Whole k = Int32.valueOfNative(2);
		Whole m = Int64.valueOfNative(1);
		
		Whole r = m.minus(k);
		
		assertEquals(Int64.valueOfNative(-1), r);
		
		assertTrue(r instanceof Int64);
	}
	
	@Test
	public void testNaturalMinusNatural(){
		Whole k = Natural.valueOfNative(2);
		Whole m = Natural.valueOfNative(1);
		
		Whole r = m.minus(k);
		
		assertEquals(Int64.valueOfNative(-1), r);
		
		assertTrue(r instanceof ScalableInt64);
	}
	
	@Test
	public void testNaturalPlusNegative(){
		Whole k = Natural.valueOfNative(2);
		Whole m = Int32.valueOfNative(-1);
		
		Whole r = m.plus(k);
		
		assertEquals(Int64.valueOfNative(1), r);
		
		assertTrue(r instanceof ScalableInt64);
		
		r = k.plus(m);
		
		assertEquals(Int64.valueOfNative(1), r);
		
		assertTrue(r instanceof ScalableInt64);
	}
	
	@Test
	public void testSacalableIn32Limit(){
		Whole k = ScalableInt32.valueOf(1);
		Whole m = ScalableInt32.valueOf(java.lang.Integer.MAX_VALUE);
		
		m.plus(k);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testPredecessorWithWhole(){
		Whole k = Natural.valueOfNative(1);
		Whole m = Integer.valueOfNative(-1);
		assertEquals( m, k.predecessor().predecessor());
	}
	
	@Test(expected = ArithmeticException.class)
	public void testPredecessorWithNaturals(){
		Natural k = Natural.valueOfNative(1);
		Integer m = Integer.valueOfNative(-1);
		assertEquals( m, k.predecessor().predecessor());
	}
	
	@Test
	public void testPredecessorWithIntegers(){
		Whole k = Integer.valueOfNative(1);
		Whole m = Integer.valueOfNative(-1);
		assertEquals( m, k.predecessor().predecessor());
	}

	@Test
	public void testScalablePredecessorLimit(){
		Whole k = Integer.valueOfNative(java.lang.Long.MIN_VALUE);
		Whole m = k.plus(Integer.valueOfNative(-1));
		Whole p = k.predecessor();
		assertEquals( m, p );
		assertTrue(p instanceof BigInt);
	}
	
	@Test(expected = ArithmeticException.class)
	public void testPredecessorLimit(){
		Whole k = Int32.valueOfNative(java.lang.Integer.MIN_VALUE);
		Whole m = k.plus(Int32.valueOfNative(-1));
		Whole p = k.predecessor();
		
	}
}
