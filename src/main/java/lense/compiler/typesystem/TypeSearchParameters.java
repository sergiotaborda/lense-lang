/**
 * 
 */
package lense.compiler.typesystem;

/**
 * 
 */
public class TypeSearchParameters {

	private String name;
	private int genericParametersCount;
	
	public TypeSearchParameters(String name, int genericParametersCount) {
		super();
		this.name = name;
		this.genericParametersCount = genericParametersCount;
	}

	public String toString(){
		return name + (genericParametersCount == 0 ? "" : "'"+ genericParametersCount);
	}
	public String getName() {
		return name;
	}

	public int getGenericParametersCount() {
		return genericParametersCount;
	}

	public int hashCode (){
		return name.hashCode() + 31 * genericParametersCount;
	}
	
	public boolean equals (Object other){
		return other instanceof TypeSearchParameters 
				&& ((TypeSearchParameters)other).name.equals(name) 
				&& ((TypeSearchParameters)other).genericParametersCount == genericParametersCount;
	}
}
