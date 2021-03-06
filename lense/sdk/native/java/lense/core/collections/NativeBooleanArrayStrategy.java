package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

 class NativeBooleanArrayStrategy implements ArrayStrategy {

	public NativeBooleanArrayStrategy(Type type) {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Array createArrayFrom(Natural size, Any seed) {
		
		boolean[] array = new boolean[ NativeNumberFactory.naturalToPrimitiveInt(size)];
		Arrays.fill(array, ((lense.core.lang.Boolean)seed).toPrimitiveBoolean());
		
		return new NativeBooleanArray(array);
	}


	
	@Override
	public Array createArrayFrom(Any[] arrayOfAny) {
		NativeBooleanArray array = new NativeBooleanArray(arrayOfAny.length);
		for(int i =0; i < arrayOfAny.length; i++) {
			array.setAtPrimitiveIndex(i, arrayOfAny[i]);
		}
		return array;
	}
	


	@Override
	public Array createArrayFrom(Sequence seq) {
		NativeBooleanArray array = new NativeBooleanArray( NativeNumberFactory.naturalToPrimitiveInt(seq.getSize()));
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.moveNext()){
			array.setAtPrimitiveIndex(i++, iterator.current());
		}
		return array;
	}


	
	@Override
	public Array createEmpty() {
		return new NativeBooleanArray(new boolean[0]);
	}

}
