/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 */
public class MethodSignature implements CallableMemberSignature<Method>{

	private final String name;
	private boolean isStatic = false;
	private final List<CallableMemberMember<Method>> parameters;
	
	public MethodSignature(String name, MethodParameter ... parameters){
		this.name = name;
		this.parameters = Arrays.asList(parameters);
	}
	
	public MethodSignature(String name, List<? extends CallableMemberMember<Method>> parameters){
		this.name = name;
		this.parameters = new ArrayList<>(parameters);
	}

	public String toString() {
		return name + "(" + parameters.toString() + ")";
	}

	/**
	 * Obtains {@link boolean}.
	 * @return the isStatic
	 */
	public boolean isStatic() {
		return isStatic;
	}

	/**
	 * Atributes {@link boolean}.
	 * @param isStatic the isStatic to set
	 */
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	
	/**
	 * @return
	 */
	public List<CallableMemberMember<Method>> getParameters() {
		return parameters;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean equals(Object other){
		return other instanceof MethodSignature && equals((MethodSignature)other);
	}
	
	private boolean equals(MethodSignature other){
		if( this.name.equals(other.name) && this.parameters.size() == other.parameters.size()){
			
			for (int i = 0; i < parameters.size(); i++ ){
				if (!this.parameters.get(i).getType().equals(other.parameters.get(i).getType())){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public int hashCode (){
		return name.hashCode() + 31 * parameters.size();
	}


	







}
