package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Signature;
import lense.core.lang.reflection.ReifiedArguments;

@Signature("[=T<lense.core.lang.Any]::")
public abstract class Maybe extends Base implements Any{

    @Constructor(paramsSignature = "")
	public static Maybe constructor(ReifiedArguments args){
		return  None.NONE; // TODO
	}
	
	public abstract boolean isPresent();
	public abstract boolean isAbsent();
	
	public abstract Maybe map(Function transformer);
}
