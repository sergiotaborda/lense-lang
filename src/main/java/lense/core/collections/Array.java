package lense.core.collections;

public class Array<T> implements List<T>{

	public static <X> Array<X> _constructor(Natural size){
		return new Array<X>();
	}

	private Array(){
		
	}
}
