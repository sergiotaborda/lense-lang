package lense.core.collections;

import lense.core.lang.reflection.ReifiedArguments;
import lense.core.lang.reflection.Type;
import lense.core.math.Natural;

public class Int32ArrayStrategy extends SizeArrayStrategy{

	ArrayStrategy resolveStrategy(ReifiedArguments args) {
		Type type = args.typeAt(Natural.valueOfNative(0));
		
		switch (type.getName().toString()){
		case "lense.core.lang.Boolean":
			return new NativeBooleanArrayStrategy(type);
		default:
			return new NativeObjectArrayStrategy(type);
		}
	}
}
