package lense.core.lang.reflection;

import java.util.ArrayList;
import java.util.List;

import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;

public class JavaReifiedArguments implements ReifiedArguments {


	public static JavaReifiedArguments getInstance() {
		return new JavaReifiedArguments();
	}

	private final List<TypeResolver> list = new ArrayList<>(1);
	
	private JavaReifiedArguments () {
	
	}
	
	
	public JavaReifiedArguments addType(TypeResolver resolver) {
		this.list.add(resolver);
		return this;
	}
	
	public JavaReifiedArguments addTypeByName(String name) {
		this.list.add(TypeResolver.byName(name));
		return this;
	}

	@Override
	public TypeResolver typeAt(Natural index) {
		return list.get(NativeNumberFactory.naturalToPrimitiveInt(index));
	}
	


}
