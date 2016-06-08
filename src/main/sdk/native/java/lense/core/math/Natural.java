package lense.core.math;

import lense.core.collections.Progression;

public class Natural extends Whole {

	
	public static Natural valueOfNative(int value){
		return new Natural();
	}
	public int toPrimitiveInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int compareTo(Natural size) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Integer minus (Natural n){
		return Int32.valueOf(this).minus(Int32.valueOf(n));
	}

	public Progression upTo(Natural other){
		return null;
	}
}
