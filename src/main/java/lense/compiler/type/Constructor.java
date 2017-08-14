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

	/**
	 * Constructor.
	 * @param parameters
	 */
	public Constructor(String name, List<? extends CallableMemberMember<Constructor>> parameters, boolean isImplicit) {
		this.parameters  = new ArrayList<>(parameters);
		this.isImplicit = isImplicit;
		this.name = name;
		
		for(CallableMemberMember<Constructor> mp : this.parameters){
			mp.setDeclaringMember(this);
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
		Constructor c = new Constructor(this.name,this.parameters, this.isImplicit);
		c.declaringType = concrete;
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
		return this.name.hashCode();
	}
	
	public boolean equals(Object other){
		return other instanceof Constructor && equals((Constructor)other);
	}
	
	public boolean equals(Constructor other){
		return other.name.equals(this.name) && this.isImplicit == other.isImplicit && this.parameters.size() == other.parameters.size()
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
}
