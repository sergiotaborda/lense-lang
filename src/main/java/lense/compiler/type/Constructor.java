/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.List;

import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class Constructor implements CallableMember<Constructor>{

	private List<CallableMemberMember<Constructor>> parameters;
	private TypeDefinition declaringType;
	private boolean isImplicit;
	private String name;
    private Visibility visibility;
    private boolean isAbstract;
	private boolean isNative;

	/**
	 * Constructor.
	 * @param parameters
	 */
	public Constructor(String name, List<? extends CallableMemberMember<Constructor>> parameters, boolean isImplicit, Visibility visibility) {
		this.parameters  = new ArrayList<>(parameters);
		this.isImplicit = isImplicit;
		this.name = name;
		this.visibility = visibility;
		
		for(CallableMemberMember<Constructor> mp : this.parameters){
			mp.setDeclaringMember(this);
		}
	}
	private Constructor(Constructor other) {
	    this.parameters = new ArrayList<>();
	    this.isImplicit = other.isImplicit;
        this.name = other.name;
        this.visibility = other.visibility;
        
        for(CallableMemberMember<Constructor> mp : other.parameters){
            
        	parameters.add(mp.attachTo(this));
     
        }
	    
	}
	public String toString(){
		return this.name + "(" +  this.parameters.toString() +")";
	}
	
	public List<CallableMemberMember<Constructor>> getParameters(){
		return parameters;
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
	public String getName() {
		return name;
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
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeMember changeDeclaringType(TypeDefinition concrete) {
		Constructor c = new Constructor(this);
		c.declaringType = concrete;
		
		for ( CallableMemberMember<Constructor> p : c.getParameters()) {
			ConstructorParameter cp = (ConstructorParameter)p;
			
			cp.setType(p.getType().changeBaseType(concrete));
		}
		
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeDefinition getDeclaringType() {
		return declaringType;
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
	public boolean isConstructor() {
		return true;
	}

	/**
	 * @param lenseTypeDefinition
	 */
	public void setDeclaringType(TypeDefinition  declaringType) {
		this.declaringType = declaringType;
	}


	@Override
	public boolean isIndexer() {
		return false;
	}

	public boolean isImplicit() {
		return isImplicit;
	}


	public void setName(String name) {
		this.name = name;
	}

	public int hashCode (){
		return this.name == null ? 0 : this.name.hashCode() + 31 * this.parameters.size();
	}
	
	public boolean equals(Object other){
		return other instanceof Constructor && equals((Constructor)other);
	}
	
	public boolean equals(Constructor other){
		return (other.name == this.name || other.name.equals(this.name)) 
				&& this.isImplicit == other.isImplicit 
				&& this.parameters.size() == other.parameters.size()
				&& this.parameters.equals(other.parameters);
	}

	public void setImplicit(boolean isImplicit) {
		this.isImplicit = isImplicit;
	}

    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }
    
    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }
	@Override
	public boolean isDefault() {
		return false;
	}
	@Override
	public void setDefault(boolean isDefault) {
		// no-op. constructors are nor overridable
		
	}
	@Override
	public boolean isOverride() {
		return false;
	}
	@Override
	public void setOverride(boolean isOverride) {
		// no-op. constructors are nor overridable
		
	}

	@Override
	public boolean isNative() {
		return isNative;
	}
	

	@Override
	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}
	
}
