package lense.core.lang;

import lense.core.collections.Iterator;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;
import lense.core.math.Natural;

public class String implements Sequence {

	@Constructor
	public static String constructor(){
		return new String("");
	}
	
	@Native
	public static String valueOfNative(java.lang.String str){
		return new String(str);
	}
	
	private java.lang.String str;
	
	@Native
	private String(java.lang.String str){
		this.str = str;
	}
	
	@Override
	public Natural getSize() {

		return Natural.valueOfNative(str.length());
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

	@Override
	public Progression getIndexes() {
		// TODO Auto-generated method stub
		return null;
	}

}
