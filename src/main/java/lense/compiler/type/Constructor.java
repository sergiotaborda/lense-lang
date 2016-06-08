/**
 * 
 */
package lense.compiler.type;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Constructor implements CallableMember<Constructor>{

	private List<CallableMemberMember<Constructor>> parameters;
	private TypeDefinition declaringType;
	private boolean isImplicit;
	private String name;

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

	

}
