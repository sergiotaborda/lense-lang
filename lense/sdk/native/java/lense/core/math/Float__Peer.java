package lense.core.math;

public class Float__Peer {

	public static Float valueOf( Whole other) {
		return BigFloat.valueOf(other);
	}

	public static Float valueOf(Real other) {
		return BigFloat.valueOf(other);
	}
	
}
