/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class Method implements CallableMember<Method> {

	private final String name;
	private boolean isStatic = false;
	private boolean isAbstract = false;
	private boolean isDefault = false;
	private boolean isOverride = false;
	private boolean isNative = false;
	
	private boolean errased = false;
	private Method superMethod;
	
	private boolean propertyBridge = false;
	
	private TypeDefinition declaringType;
	private MethodReturn returnParameter;
	private List<CallableMemberMember<Method>> parameters = new ArrayList<>();
	
	private List<TypeVariable> methodFreeGenericTypes = new ArrayList<>();
    private Visibility visibility;
	
	public List<TypeVariable> getFreeGenericTypes() {
		return methodFreeGenericTypes;
	}
	
	public Method(boolean isPropertyBridge, Visibility visibility,String name, MethodReturn returnParameter, MethodParameter ... parameters){
		this(isPropertyBridge,visibility,name, returnParameter, Arrays.asList(parameters));
	}
	
	public Method(boolean isPropertyBridge,Visibility visibility,String name, MethodReturn returnParameter, List<? extends CallableMemberMember<Method>>  parameters){
		this.name = name;
		this.visibility = visibility;
		
		this.propertyBridge = isPropertyBridge;
		
		this.returnParameter = returnParameter;
		this.returnParameter.setDeclaringMember(this);
		
		this.parameters = new ArrayList<>(parameters);
		for(CallableMemberMember<Method> mp : this.parameters){
			mp.setDeclaringMember(this);
		}
	}
	
	public Method(Method method) {
		this(method.propertyBridge, method.visibility, method.name, method.returnParameter, method.parameters);
		this.isAbstract= method.isAbstract;
		this.isStatic= method.isStatic;
		this.isDefault= method.isDefault;
		this.isOverride= method.isOverride;
		this.isNative= method.isNative;
		this.methodFreeGenericTypes = method.methodFreeGenericTypes;
	}

	public void setDeclaringType(TypeDefinition type){
		this.declaringType = type;
	}
	
	public void add(TypeVariable parameter){
		methodFreeGenericTypes.add(parameter);
	}
	
	public MethodSignature getSignature() {
		return new MethodSignature(name, parameters);
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
				if (!this.parameters.get(i).typeEquals(other.parameters.get(i))){
					return false;
				}
			}
			return true;
		}
		
		return false;
	}

	public int hashCode(){
		return this.name.hashCode() + 31 * this.parameters.size();
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
		
		Method m = new Method(this);
	
		m.setDeclaringType(concrete);
		m.setReturn(new MethodReturn(this.returnParameter.getType().changeBaseType(concrete)));
		m.parameters = m.parameters.stream().map( p -> new MethodParameter((MethodParameter)p, p.getType().changeBaseType(concrete))).collect(Collectors.toList());
		
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

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isOverride() {
		return isOverride;
	}

	public void setOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public boolean isPropertyBridge() {
		return propertyBridge;
	}

	public void setPropertyBridge(boolean propertyBridge) {
		this.propertyBridge = propertyBridge;
	}

	public Method getSuperMethod() {
		return superMethod;
	}

	public void setSuperMethod(Method superMethod) {
		this.superMethod = superMethod;
	}

    public boolean isErrased() {
        return errased;
    }

    public void setErased(boolean errased) {
        this.errased = errased;
    }


}
