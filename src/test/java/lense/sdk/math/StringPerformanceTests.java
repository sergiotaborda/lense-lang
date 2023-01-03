package lense.sdk.math;

import org.junit.Test;

import lense.core.lang.java.NativeString;

public class StringPerformanceTests {


	@Test 
	public void testConcatTen() {
		int iterationsCount = 100;
		int topMAx = 1_000 ;
		//warming
		System.out.println("warming...");
		
		for(int i = 0; i < iterationsCount; i++){
			concatenate(topMAx);
		}
		
		System.out.println("timing...");
		for (int max = 10; max <= 100_000; max*=10){
			System.out.println("...max= " + max);
			long time = System.currentTimeMillis();
			for(int i = 0; i < iterationsCount; i++){
				concatenate(max);
			}
			System.out.println("...... concat " + max  + ": " + (System.currentTimeMillis() *1.0 - time) / iterationsCount + " ms");

		}
		System.out.println("ended");

	}
	
	private void concatenate(int count) {
		var a = NativeString.valueOfNative("a");
		var s = a;
		
		for (int i =0; i < count ; i++) {
			s = s.concat(a);
		}
	}
		
	

}
