package lense.core.lang.reflection;

import lense.core.math.Natural;

public class JavaReifiedArguments implements ReifiedArguments {


	private lense.core.lang.reflection.Type[] types;
	
	public JavaReifiedArguments (lense.core.lang.reflection.Type ... types) {
		this.types = types;
	}
	
	public Type typeByParameterValiableName(Natural name){
		
		int index = name.toPrimitiveInt();
		
		if (index < 0 || index > types.length - 1) {
			throw new RuntimeException("Reified type not found for parameter " + name.toString());
		}
		return types[index];
	}
}
