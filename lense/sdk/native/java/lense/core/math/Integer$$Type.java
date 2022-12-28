package lense.core.math;

import lense.core.collections.Sequence;
import lense.core.lang.Any;
import lense.core.lang.String;
import lense.core.lang.java.NativeString;
import lense.core.lang.reflection.Type;

//@Placeholder
public class Integer$$Type extends Type {
	
	@lense.core.lang.java.MethodSignature( 
			returnSignature = "lense.core.math.Integer" , 
			paramsSignature = "lense.core.math.Integer,lense.core.math.Integer" , 
			override = false , 
		    declaringType = "lense.core.lang.Summable")
	public Integer sum (Any a, Any b) {
		return ((Integer)a).plus((Integer)b);
	}

	@Override
	public Type duplicate() {
		return new Integer$$Type();
	}

	@Override
	public String getName() {
		return NativeString.valueOfNative("lense.core.math.Integer$$Type");
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
