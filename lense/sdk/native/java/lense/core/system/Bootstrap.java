package lense.core.system;

import lense.core.collections.Array;
import lense.core.lang.reflection.JavaReifiedArguments;

public class Bootstrap {

	public static void main(String[] args) {
		
		ConsoleApplication app = null;
		app.setArguments(Array.fromNative(JavaReifiedArguments.getInstance().addType("lense.core.lang.String"), args, s -> lense.core.lang.String.valueOfNative(s)));
		app.onStart();
		
	
	}

}
