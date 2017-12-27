/**
 * 
 */
package lense.compiler.typesystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class TypeKey {

	private List<TypeVariable> genericTypeParameters;
	private String name;
	

	public TypeKey(List<TypeVariable> genericTypeParameters, String name) {
		super();
		this.genericTypeParameters = genericTypeParameters == null ? new ArrayList<>(0) : genericTypeParameters;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString(){
		return name + "'" + genericTypeParameters.size();
	}
	public List<TypeVariable> getGenericTypeParameters() {
		return genericTypeParameters;
	}

	public boolean equals(Object other){
		return other instanceof TypeKey 
				&& ((TypeKey)other).name.equals(this.name) 
				&& Arrays.equals(((TypeKey)other).genericTypeParameters.toArray(), this.genericTypeParameters.toArray());
	}
	
	public int hashCode (){
		return name.hashCode() + 31 * genericTypeParameters.size();
	}
	
}
