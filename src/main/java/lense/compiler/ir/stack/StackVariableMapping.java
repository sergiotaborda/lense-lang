package lense.compiler.ir.stack;

import java.util.HashMap;
import java.util.Map;

public class StackVariableMapping {

	int last = -1;
	Map<String, Integer> mapping = new HashMap<>();

	public Integer get(String name) {
		return mapping.get(name);
	}

	public Integer putIncrement(String name) {
		last++;
		mapping.put(name,  last);
		return last;
		
	}
}
