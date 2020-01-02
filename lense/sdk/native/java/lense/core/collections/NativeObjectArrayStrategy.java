package lense.core.collections;

import java.util.Arrays;

import lense.core.lang.Any;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

 class NativeObjectArrayStrategy implements ArrayStrategy {

	private final Type argumentType;

	public NativeObjectArrayStrategy(Type argumentType) {
		this.argumentType = argumentType;
	}

	@Override
	public Array createArrayFrom(Natural size, Any seed) {
		Any[] array = new Any[NativeNumberFactory.naturalToPrimitiveInt(size)];
		Arrays.fill(array,seed);

		return new NativeObjectArray(array, argumentType);
	}

	@Override
	public Array createArrayFrom(Any[] arrayOfAny) {
		return new NativeObjectArray(arrayOfAny,argumentType);
	}

	@Override
	public Array createArrayFrom(Sequence seq) {
		NativeObjectArray array = new NativeObjectArray(seq.getSize(), argumentType);
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.moveNext()){
			array.setAtPrimitiveIndex(i++, iterator.current());
		}
		return array;
	}

	@Override
	public Array createEmpty() {
		return new NativeObjectArray(new Any[0], argumentType);
	}
}
