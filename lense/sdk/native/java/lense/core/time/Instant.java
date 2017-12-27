package lense.core.time;

import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;

public class Instant extends Base {

	@Constructor(paramsSignature = "")
	public static Instant constructor(){
		return new Instant();
	}
}
