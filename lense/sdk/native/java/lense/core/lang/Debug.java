package lense.core.lang;

import lense.core.lang.Any;
import lense.core.lang.HashValue;
import lense.core.lang.String;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.SingletonObject;
import lense.core.lang.reflection.Type;
import lense.core.lang.reflection.TypeResolver;
import lense.core.math.Natural;

@SingletonObject
public final class Debug implements Any {

	public static Debug DEBUG = new Debug();
	public static final TypeResolver TYPE_RESOLVER = TypeResolver.lazy(() -> new Type(Debug.class));
	
	@Constructor(paramsSignature = "")
	public static Debug constructor(){
		return DEBUG;
	}

	public boolean equalsTo(Any other) {
		return other instanceof Debug;
	}

	@Override
	public HashValue hashValue() {
		return new HashValue(0);
	}

	@Override
	public String asString() {
		return String.valueOfNative("DEBUG");
	}


	public void log(String msg) {
		System.out.println(msg.toString());
	}

	@Override
	public Type type() {
		 return TYPE_RESOLVER.resolveType();
	}

}
