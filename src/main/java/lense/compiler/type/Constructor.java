/**
 * 
 */
package lense.compiler.type;

import java.util.List;

/**
 * 
 */
public class Constructor implements CallableMember{

	private List<MethodParameter> parameters;
	private TypeDefinition declaringType;
	private boolean isImplicit;

	/**
	 * Constructor.
	 * @param parameters
	 */
	public Constructor(List<MethodParameter> parameters, boolean isImplicit) {
		this.parameters  = parameters;
		this.isImplicit = isImplicit;
	}

	
	public List<MethodParameter> getParameters(){
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
		return "";
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
		Constructor c = new Constructor(this.parameters, this.isImplicit);
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

	

}
