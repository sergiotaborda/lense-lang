package lense.core.math;

import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.lang.java.Constructor;
import lense.core.lang.java.Native;

public class Natural extends Whole {

	@Constructor
	public static Natural constructor(){
		return Natural.valueOfNative(0);
	}
	
	@Native
	public static Natural valueOfNative(int value){
		return new Natural(value);
	}

	private int value;
	
	private Natural(int value){
		this.value = value;
	}
	
	@Native
	public int toPrimitiveInt() {
		return value;
	}

	public int compareTo(Natural other) {
		return this.value  == other.value ? 0 :(this.value < other.value ? -1 : 1);
	}
	
	public Integer minus (Natural n){
		return Int32.valueOf(this).minus(Int32.valueOf(n));
	}

	public Progression upTo(Natural other){
		return new NativeProgression(value, other.value);
	}
}
