package lense.core.math;

import lense.core.collections.Sequence;
import lense.core.lang.String;
import lense.core.lang.java.NativeString;
import lense.core.lang.reflection.Type;

//@Placeholder
public class Whole$$Type extends Type {
	
	@lense.core.lang.java.MethodSignature( 
			returnSignature = "lense.core.math.Whole" , 
			paramsSignature = "lense.core.math.Whole,lense.core.math.Whole" , 
			override = false , 
		    declaringType = "lense.core.lang.Summable")
	public Whole sum (Whole a, Whole b) {
		return a.plus(b);
	}

	@Override
	public Type duplicate() {
		return new Whole$$Type();
	}

	@Override
	public String getName() {
		return NativeString.valueOfNative("lense.core.math.Whole$$Type");
	}

	@Override
	protected Sequence loadMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Sequence loadProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
