package lense.core.lang.reflection;

import java.util.HashMap;
import java.util.Map;
import lense.core.lang.reflection.Type;

public class JavaReifiedArguments implements ReifiedArguments {

	public static JavaReifiedArgumentsPair pair(String name, lense.core.lang.reflection.Type type) {
		return new JavaReifiedArgumentsPair( name,  type);
	}
	
	private Map<String , lense.core.lang.reflection.Type > types = new HashMap<>();
	
	public JavaReifiedArguments (JavaReifiedArgumentsPair ... pairs) {
		for (JavaReifiedArgumentsPair p : pairs) {
			types.put(p.name, p.type);
		}
	}
	
	public Type typeByParameterValiableName(lense.core.lang.String name){
		
		Type type = types.get(name.toString());
		
		if (type == null) {
			throw new RuntimeException("Reified type not found for parameter " + name.toString());
		}
		
		return type;
	}
}
