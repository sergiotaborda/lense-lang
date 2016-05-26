package lense.core.lang;

import lense.core.collections.Iterator;
import lense.core.collections.Sequence;
import lense.core.math.Natural;

public class String implements Sequence {

	@NativeInteraction
	public static String valueOfNative(java.lang.String str){
		return new String(str);
	}
	
	private java.lang.String str;
	
	private String(java.lang.String str){
		this.str = str;
	}
	
	@Override
	public Natural getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator getIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public lense.core.lang.Character get(Natural index) {
		// TODO Auto-generated method stub
		return null;
	}

}
