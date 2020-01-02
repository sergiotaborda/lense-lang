package lense.core.collections;

import lense.core.lang.reflection.ReifiedArguments;
import lense.core.math.Natural;
import lense.core.math.Natural64;

abstract class SizeArrayStrategy {

	private static final Int32ArrayStrategy int32ArrayStrategy = new Int32ArrayStrategy();
	
	 static SizeArrayStrategy resolveSizeStrategy(Natural size) {
		 
		 System.out.println("Creating an array of size "  + size.toString());
		 if (size.compareWith(Natural64.INT32_MAX).isSmaller()) {
			 return resolveStandardSizeStrategy();
		 } else {
			 throw new UnsupportedOperationException("Arrays bigger than int32 length are not supported yet");
		 }
	 }
	 
	 static Int32ArrayStrategy resolveStandardSizeStrategy() {
		 return int32ArrayStrategy;
	 }
	
	 
	abstract ArrayStrategy resolveStrategy(ReifiedArguments args);
	
}
