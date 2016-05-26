/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class Method implements CallableMember {

	private final String name;
	private boolean isStatic = false;
	private boolean isAbstract = false;
	private TypeDefinition declaringType;
	private MethodReturn returnParameter;
	private List<MethodParameter> parameters;
	
	private List<IntervalTypeVariable> methodFreeGenericTypes = new ArrayList<>();
	
	public List<IntervalTypeVariable> getFreeGenericTypes() {
		return methodFreeGenericTypes;
	}
	
	public Method(String name, MethodReturn returnParameter, MethodParameter ... parameters){
		this(name, returnParameter, Arrays.asList(parameters));
	}
	
	public Method(String name, MethodReturn returnParameter, List<MethodParameter>  parameters){
		this.name = name;
		
		this.returnParameter = returnParameter;
		this.returnParameter.setDeclaringMethod(this);
		
		this.parameters = new ArrayList<>(parameters);
		for(MethodParameter mp : this.parameters){
			mp.setDeclaringMethod(this);
		}
	}
	
	public void setDeclaringType(TypeDefinition type){
		this.declaringType = type;
	}
	
	public void add(IntervalTypeVariable parameter){
		methodFreeGenericTypes.add(parameter);
	}
	
	public boolean conformsTo(MethodSignature signature){
		return false;
	}
	
	
	public String toString(){
		return declaringType.getName() + "." + name + "(" + parameters + ") :" + returnParameter;
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
	public String getName() {
		return name;
	}
	
	public TypeVariable getReturningType(){
		return returnParameter.getType();
	}

	public TypeDefinition getDeclaringType(){
		return declaringType;
	}
	
	/**
	 * @return
	 */
	public List<MethodParameter> getParameters() {
		return parameters;
	}

	/**
	 * @param methodFreeBoxedTypeParameter
	 */
	public void setReturn(MethodReturn param) {
		param.setDeclaringMethod(this);
		this.returnParameter = param;
	}

	
	public boolean equals(Object other){
		return this == other || (  other instanceof Method && equals((Method)other));
	}

	public boolean equals(Method other){
		if( this.name.equals(other.getName()) && this.parameters.size() == other.parameters.size()){
			
			for (int i = 0; i < this.parameters.size(); i++){
				if (!this.parameters.get(i).equals(other.parameters.get(i))){
					return false;
				}
			}
			return true;
		}
		
		return false;
	}

	public int hashCode(){
		return this.name.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSynthetic() {
		return false;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isField() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isProperty() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMethod() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Method changeDeclaringType(TypeDefinition concrete) {
		
		MethodReturn r = new MethodReturn(this.returnParameter.getType().changeBaseType(concrete));
		Method m = new Method(this.name, r, this.parameters);
		m.setDeclaringType(concrete);
		return m;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConstructor() {
		return false;
	}

	@Override
	public boolean isIndexer() {
		return false;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}




}
