/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.util.HashMap;
import java.util.Map;

import compiler.typesystem.TypeDefinition;
import compiler.typesystem.TypeResolver;
import compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class JavaTypeResolver implements TypeResolver{

	
	static JavaTypeResolver me = new JavaTypeResolver();
	
	private Map<String, TypeDefinition> types = new HashMap<>();
	/**
	 * @return
	 */
	public static TypeResolver getInstance() {
		return me;
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition resolveTypeByName(TypeSearchParameters filter) {
		String name = filter.getName();
		if (!name.startsWith("java")){
			return null;
		}
		
		try {
			return fromClass(Class.forName(name, false, this.getClass().getClassLoader()));
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	

	
	/**
	 * @param forName
	 * @return
	 */
	protected TypeDefinition fromClass(Class<?> type) {
	
		 if (!types.containsKey(type.getName())){
			 TypeDefinition t = new JavaType(type, this);
			 types.put(type.getName(), t);
			 return t;
		 }  else {
			 return types.get(type.getName());
		 }
	}



	
}
