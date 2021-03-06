package lense.core.collections;

import lense.core.lang.reflection.ReifiedArguments;
import lense.core.lang.reflection.Type;
import lense.core.math.Natural64;

public class Int32ArrayStrategy extends SizeArrayStrategy{

	ArrayStrategy resolveStrategy(ReifiedArguments args) {
		Type type = args.typeAt(Natural64.ZERO).resolveType();
		
		switch (type.getName().toString()){
		case "lense.core.lang.Boolean":
			return new NativeBooleanArrayStrategy(type);
		case "lense.core.lang.Maybe":
			return new NativeMaybeArrayStrategy(type);
		default:
			return new NativeObjectArrayStrategy(type);
		}
	}
}
