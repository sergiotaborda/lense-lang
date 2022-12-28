package lense.core.system;

import lense.core.collections.Array;
import lense.core.lang.java.JavaReifiedArguments;
import lense.core.lang.java.NativeString;

public class Bootstrap {

	public static void main(String[] args) {
		
		ConsoleApplication app = null;
		app.setArguments(Array.fromNative(JavaReifiedArguments.getInstance().addType(lense.core.lang.java.NativeString.TYPE_RESOLVER), args, s -> NativeString.valueOfNative(s)));
		app.onStart();
		
	
	}

}
