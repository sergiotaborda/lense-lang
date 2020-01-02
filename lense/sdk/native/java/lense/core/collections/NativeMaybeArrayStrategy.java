package lense.core.collections;

import lense.core.lang.Any;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

public class NativeMaybeArrayStrategy implements ArrayStrategy {

	private Type type;

	public NativeMaybeArrayStrategy(Type type) {
		this.type = type;
		System.out.println("Created a Maybe array for type " + innerTypeName());
	}
	
	@Override
	public Array createArrayFrom(Natural size, Any seed) {
		return new NativeMaybeArray(type, NativeNumberFactory.naturalToPrimitiveInt(size));
	}

	private String innerTypeName() {
		return type.getName().toString();
	}

	@Override
	public Array createArrayFrom(Any[] arrayOfAny) {
		NativeMaybeArray array = new NativeMaybeArray(type,arrayOfAny.length);
		for(int i =0; i < arrayOfAny.length; i++) {
			array.setAtPrimitiveIndex(i, arrayOfAny[i]);
		}
		return array;
	}

	@Override
	public Array createArrayFrom(Sequence seq) {
		NativeMaybeArray array = new NativeMaybeArray(type, NativeNumberFactory.naturalToPrimitiveInt(seq.getSize()));
		Iterator iterator = seq.getIterator();
		int i=0;
		while(iterator.moveNext()){
			array.setAtPrimitiveIndex(i++, iterator.current());
		}
		return array;
	}

	@Override
	public Array createEmpty() {
		return new NativeMaybeArray(type,0);
	}

}
