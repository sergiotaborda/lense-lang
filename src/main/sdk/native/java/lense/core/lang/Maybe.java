package lense.core.lang;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public abstract class Maybe extends Base implements Any{

	@Constructor
	public static Maybe none(){
		return  None.Null; // TODO
	}
	
	public abstract boolean isPresent();
	public abstract boolean isAbsent();
}
