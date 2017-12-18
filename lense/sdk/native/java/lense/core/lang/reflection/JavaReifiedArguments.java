package lense.core.lang.reflection;

import java.util.ArrayList;
import java.util.List;

import lense.core.math.Natural;

public class JavaReifiedArguments implements ReifiedArguments {


	public static JavaReifiedArguments getInstance() {
		return new JavaReifiedArguments();
	}
	
	private List<lense.core.lang.reflection.Type> types = new ArrayList<Type>(2);
	
	private JavaReifiedArguments () {
	
	}
	
	public JavaReifiedArguments addType(String name) {
		types.add(Type.fromName(name));
		return this;
	}
	
	private JavaReifiedArguments addType(Type type) {
		types.add(type);
		return this;
	}
	
	public Type typeAt(Natural index){
		
		int jindex = index.toPrimitiveInt();
		
		if (jindex < 0 || jindex > types.size() - 1) {
			throw new RuntimeException("Reified type not found for parameter " + index.toString());
		}
		return types.get(jindex);
	}
	
	
	public ReifiedArguments fromIndex(Natural index){
		return new JavaReifiedArguments().addType(types.get(index.toPrimitiveInt()));
	}
}
