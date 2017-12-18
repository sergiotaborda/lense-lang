package lense.core.collections;

import lense.core.lang.reflection.ReifiedArguments;
import lense.core.math.Natural;

abstract class SizeArrayStrategy {

	private static final Int32ArrayStrategy int32ArrayStrategy = new Int32ArrayStrategy();
	
	 static SizeArrayStrategy resolveSizeStrategy(Natural size) {
		 
		 if (size.isInInt32Range()) {
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
