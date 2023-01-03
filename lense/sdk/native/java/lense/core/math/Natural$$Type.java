package lense.core.math;

import lense.core.collections.Sequence;
import lense.core.lang.Any;
import lense.core.lang.String;
import lense.core.lang.java.NativeString;
import lense.core.lang.reflection.Type;

//@Placeholder
public class Natural$$Type extends Type {
	
	@lense.core.lang.java.MethodSignature( 
			returnSignature = "lense.core.math.Natural" , 
			paramsSignature = "lense.core.math.Natural,lense.core.math.Natural" , 
			override = false , 
		    declaringType = "lense.core.lang.Summable")
	public Natural sum (Any a, Any b) {
		return ((Natural)a).plus((Natural)b);
	}

	@Override
	public Type duplicate() {
		return new Natural$$Type();
	}

	@Override
	public String getName() {
		return NativeString.valueOfNative("lense.core.math.Natural$$Type");
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
