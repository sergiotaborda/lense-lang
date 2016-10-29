package lense.core.math;

import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public class Number extends Base implements Any{

	@Constructor
	public static Number constructor (){
		return new Number();
	}


}
