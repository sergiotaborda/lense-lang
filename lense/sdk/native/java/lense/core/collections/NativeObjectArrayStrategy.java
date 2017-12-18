package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.reflection.Type;
import lense.core.math.Natural;

 class NativeObjectArrayStrategy implements ArrayStrategy {

	private Type argumentType;

	public NativeObjectArrayStrategy(Type argumentType) {
		this.argumentType = argumentType;
	}

	@Override
	public Array createArrayFrom(Natural size, Any seed) {
		Any[] array = new Any[size.toPrimitiveInt()];
		Arrays.fill(array,seed);

		return new NativeObjectArray(array);
	}

	@Override
	public Array createArrayFrom(Any[] arrayOfAny) {
		return new NativeObjectArray(arrayOfAny);
	}

	@Override
	public Array createArrayFrom(Sequence seq) {
		NativeObjectArray array = new NativeObjectArray(seq.getSize());
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.moveNext()){
			array.setAtPrimitiveIndex(i++, iterator.current());
		}
		return array;
	}

}
