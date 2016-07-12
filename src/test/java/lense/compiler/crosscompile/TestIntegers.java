package lense.compiler.crosscompile;

import static org.junit.Assert.*;

import org.junit.Test;

import lense.core.math.Int16;
import lense.core.math.Int32;
import lense.core.math.Int64;

public class TestIntegers {

	@Test 
	public void testInt16Plus()  {
		Int16 i = Int16.valueOfNative(50);
  		Int16 j = Int16.valueOfNative(134);
  		
  		Int16 x = i.plus(j);
  		
  		assertEquals(Int16.valueOfNative(184), x);
	}
	
	@Test (expected = ArithmeticException.class)
	public void testInt16Overflow()  {
		Int16 i = Int16.valueOfNative(32767);
  		Int16 j = Int16.valueOfNative(1);
  		
  		i.plus(j);
	}
	
	@Test 
	public void testInt32Plus()  {
		Int32 i = Int32.valueOfNative(50);
		Int32 j = Int32.valueOfNative(134);
  		
  		Int32 x = i.plus(j);
  		
  		assertEquals(Int32.valueOfNative(184), x);
	}
	
	@Test (expected = ArithmeticException.class)
	public void testInt32Overflow()  {
		Int32 i = Int32.valueOfNative(Integer.MAX_VALUE);
		Int32 j = Int32.valueOfNative(1);
  		
		i.plus(j);
	}
	
	@Test 
	public void testInt64Plus()  {
		Int64 i = Int64.valueOfNative(50);
		Int64 j = Int64.valueOfNative(134);
  		
		Int64 x = i.plus(j);
  		
  		assertEquals(Int64.valueOfNative(184), x);
	}
	
	@Test (expected = ArithmeticException.class)
	public void testInt64Overflow()  {
		Int64 i = Int64.valueOfNative(Long.MAX_VALUE);
		Int64 j = Int64.valueOfNative(1);
  		
		i.plus(j);
	}
}
