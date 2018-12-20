package lense.sdk.math;

import java.math.BigInteger;

import org.junit.Ignore;
import org.junit.Test;

import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

public class PerformanceTests {


	@Test 
	public void testRunTimePlusOne() {
		int iterationsCount = 100;
		long topMAx = 1_000_000 ;
		//warming
		System.out.println("warming...");
		
		for(int i = 0; i < iterationsCount; i++){
			//iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(topMAx),BigInteger.ONE);
			iterateNatural(NativeNumberFactory.newNatural(0), NativeNumberFactory.newNatural(topMAx), Natural64.ONE);
		}
		
		System.out.println("timing...");
		for (long max = 10; max <= 1_000_000_000; max*=10){
			System.out.println("...max= " + max);
			long time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLong(0, max, 1);
			}
			System.out.println("......long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLongObject(new Long(0), new Long(max), new Long(1));
			}
			System.out.println("......Long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

			
//			time = System.currentTimeMillis();
//			for(int i = 0; i < iterationsCount; i++){
//				iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(max));
//			}
//			System.out.println("......BigInteger : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount+ " ms");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateNatural(Natural64.ZERO, NativeNumberFactory.newNatural(max), Natural64.ONE);
			}
			System.out.println("......Natural : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateNaturalSucessor(Natural64.ZERO, NativeNumberFactory.newNatural(max));
			}
			System.out.println("......Natural Successor : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

		}
		System.out.println("ended");

	}
	
	@Test @Ignore
	public void testRunTimePlus1000() {
		int iterationsCount = 100;
		long topMAx = 1_000_000 ;
		//warming
		System.out.println("warming...");
		
		for(int i = 0; i < iterationsCount; i++){
			//iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(topMAx),BigInteger.ONE);
			iterateNatural(NativeNumberFactory.newNatural(0), NativeNumberFactory.newNatural(topMAx), Natural64.ONE);
		}
		
		System.out.println("timing...");
		for (long max = 10; max <= 1_000_000_000_000L; max*=10){
			System.out.println("...max= " + max);
			long time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLong(0, max, 1000);
			}
			System.out.println("......long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLongObject(new Long(0), new Long(max), new Long(1000));
			}
			System.out.println("......Long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

			
//			time = System.currentTimeMillis();
//			for(int i = 0; i < iterationsCount; i++){
//				iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(max), BigInteger.valueOf(1000));
//			}
//			System.out.println("......BigInteger : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount+ " ms");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateNatural(Natural64.ZERO, NativeNumberFactory.newNatural(max), NativeNumberFactory.newNatural(1000));
			}
			System.out.println("......Natural : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

		}
		
//		for (BigInteger max = new BigInteger("10000000000"); max.compareTo(new BigInteger("1000000000000")) <=0; max = max.multiply(BigInteger.TEN)){
//			System.out.println("...max= " + max);
//			long time = System.currentTimeMillis();
//			for(int i = 0; i < iterationsCount; i++){
//				iterateBigInteger(BigInteger.ZERO, max, BigInteger.valueOf(1000));
//			}
//			System.out.println("......BigInteger : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount+ " ms");
//
//			time = System.currentTimeMillis();
//			for(int i = 0; i < iterationsCount; i++){
//				iterateNatural(Natural.ZERO, Natural.valueOf(max), Natural.valueOf(1000));
//			}
//			System.out.println("......Natural : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");
//
//		}
		System.out.println("ended");

	}
	
	@Test @Ignore
	public void testRunTimePlusProporcional() {
		int iterationsCount = 100;
		long topMAx = 1_000_000 ;
		//warming
		System.out.println("warming...");
		
		for(int i = 0; i < iterationsCount; i++){
			//iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(topMAx),BigInteger.ONE);
			iterateNatural(NativeNumberFactory.newNatural(0), NativeNumberFactory.newNatural(topMAx), Natural64.ONE);
		}
		
		System.out.println("timing...");
		for (long max = 10; max <= 1_000_000_000; max*=10){
			System.out.println("...max= " + max);
			long time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLong(0, max, max / 10);
			}
			System.out.println("......long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms/op");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateLongObject(new Long(0), new Long(max), new Long(max/10));
			}
			System.out.println("......Long : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms/op");

			
			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateBigInteger(BigInteger.ZERO, BigInteger.valueOf(max), BigInteger.valueOf(max/10));
			}
			System.out.println("......BigInteger : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount+ " ms/op");

			time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				iterateNatural(Natural64.ZERO, NativeNumberFactory.newNatural(max), NativeNumberFactory.newNatural(max / 10));
			}
			System.out.println("......Natural : " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms/op");

			

		}
		System.out.println("ended");

	}
	
	private long iterateLong(long minValue, long maxValue, long setp) {
		long c = minValue;
		while(c < maxValue){
			c += setp;
		}
		return c;
	}

	private Long iterateLongObject(Long minValue, Long maxValue, Long setp) {
		Long c = minValue;
		while(c < maxValue){
			c = new Long(c.longValue() + setp.longValue());
		}
		return c;
	}
	
	private BigInteger iterateBigInteger(BigInteger minValue, BigInteger maxValue, BigInteger step) {
		BigInteger c = minValue;
		while(c.compareTo(maxValue) < 0){
			c = c.add(step);
		}
		return c;
	}

	private Natural iterateNatural(Natural minValue, Natural maxValue, Natural step) {
		Natural c = minValue;

		while(c.isLessThen(maxValue)){
			c = c.plus(step);
		}
		return c;
	}

	private Natural iterateNaturalSucessor(Natural minValue, Natural maxValue) {
		Natural c = minValue;

		while(c.isLessThen(maxValue)){
			c = c.successor();
		}
		return c;
	}
	

}
