/**
 * 
 */
package lense.compiler;

import java.lang.reflect.Method;

/**
 * 
 */
public class AstNodeProperty {

	private String name;
	private Method acessor;
	private Method modifier;

	public AstNodeProperty(String name, Method acessor, Method modifier){
		this.name = name;
		this.acessor = acessor;
		this.modifier = modifier;

		this.acessor.setAccessible(true);
		this.modifier.setAccessible(true);
	}

	public String getName(){
		return name;
	}


	@Override
	public boolean equals(Object obj) {
		return (obj instanceof AstNodeProperty) && equalsProperty((AstNodeProperty)obj); 
	}

	private boolean equalsProperty(AstNodeProperty other) {
		return this.name.equals(other.name);
	}
	
	public int hashCode(){
		return name.hashCode();
	}

	public Object get(Object instance){
		try {
			return acessor.invoke(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	public void set(Object instance, Object value){
		try {
			if (value != null && !modifier.getParameterTypes()[0].isInstance(value)){
				throw new IllegalArgumentException(value + " is of type " + value.getClass() + " expected " + modifier.getParameterTypes()[0]);
			}

			modifier.invoke(instance, new Object[]{value});
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
