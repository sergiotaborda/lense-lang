package lense.core.system;

import lense.core.collections.Array;

public class Bootstrap {

	public static void main(String[] args) {
		
		ConsoleApplication app = null;
		app.setArguments(Array.fromNative(args, s -> lense.core.lang.String.valueOfNative(s)));
		app.onStart();
		
	
	}

}
