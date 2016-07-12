/**
 * 
 */
package lense.compiler.typesystem;

import java.util.Optional;

/**
 * 
 */
public class TypeSearchParameters {

	private String name;
	private Optional<Integer> genericParametersCount;
	
	public TypeSearchParameters(String name, int genericParametersCount) {
		super();
		this.name = name;
		this.genericParametersCount = Optional.of(genericParametersCount);
	}
	
	public TypeSearchParameters(String name) {
		super();
		this.name = name;
		this.genericParametersCount = Optional.empty();
	}

	public String toString(){
		return name + (genericParametersCount.map(s -> s == 0).orElse(true) ? "" : "'"+ genericParametersCount);
	}
	public String getName() {
		return name;
	}

	public Optional<Integer> getGenericParametersCount() {
		return genericParametersCount;
	}

	public int hashCode (){
		return name.hashCode() + 31 * genericParametersCount.orElse(-1);
	}
	
	public boolean equals (Object other){
		return other instanceof TypeSearchParameters 
				&& ((TypeSearchParameters)other).name.equals(name) 
				&& ((TypeSearchParameters)other).genericParametersCount.isPresent() ? genericParametersCount.map(s -> ((TypeSearchParameters)other).genericParametersCount.get().equals(s)).orElse(false) : !genericParametersCount.isPresent();
	}
}
