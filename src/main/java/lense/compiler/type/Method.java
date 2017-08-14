/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class Method implements CallableMember<Method> {

	private final String name;
	private boolean isStatic = false;
	private boolean isAbstract = false;
	private TypeDefinition declaringType;
	private MethodReturn returnParameter;
	private List<CallableMemberMember<Method>> parameters;
	
	private List<IntervalTypeVariable> methodFreeGenericTypes = new ArrayList<>();
    private Visibility visibility;
	
	public List<IntervalTypeVariable> getFreeGenericTypes() {
		return methodFreeGenericTypes;
	}
	
	public Method(String name, MethodReturn returnParameter, MethodParameter ... parameters){
		this(name, returnParameter, Arrays.asList(parameters));
	}
	
	public Method(String name, MethodReturn returnParameter, List<? extends CallableMemberMember<Method>>  parameters){
		this.name = name;
		
		this.returnParameter = returnParameter;
		this.returnParameter.setDeclaringMember(this);
		
		this.parameters = new ArrayList<>(parameters);
		for(CallableMemberMember<Method> mp : this.parameters){
			mp.setDeclaringMember(this);
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
	    if (parameters.isEmpty()){
	        return  name + "( ) :" + returnParameter.getType().getSymbol().orElse(returnParameter.getType().getTypeDefinition().getName());
	    } else {
	        return  name + "(" + parameters + ") :" + returnParameter.getType().getSymbol().orElse(returnParameter.getType().getTypeDefinition().getName());
	    }
	
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
	public List<CallableMemberMember<Method>> getParameters() {
		return parameters;
	}

	/**
	 * @param methodFreeBoxedTypeParameter
	 */
	public void setReturn(MethodReturn param) {
		param.setDeclaringMember(this);
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

    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }


}
