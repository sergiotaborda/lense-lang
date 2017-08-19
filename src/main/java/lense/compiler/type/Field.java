/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class Field implements TypeMember {

	private TypeVariable type;
	private String name;
	private TypeDefinition declaringType;
	private boolean isFinal;
    private Visibility visibility;
	 
	/**
	 * Constructor.
	 * @param typeAdapter
	 * @param name
	 * @param fromClass
	 */
	public Field(String name, TypeVariable type, boolean isFinal) {
		this.type = type;
		this.name = name;
		this.isFinal = isFinal;
	}

	public TypeVariable getReturningType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String toString(){
		return name + ":" + type.toString();
	}

	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	public boolean isFinal(){
		return isFinal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isField() {
		return true;
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

	public void setDeclaringType(TypeDefinition declaringType) {
		this.declaringType = declaringType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TypeMember changeDeclaringType(TypeDefinition concrete) {
		TypeVariable t = this.type.changeBaseType(concrete);
		
		Field f = new Field(this.name, t, this.isFinal);
		f.setDeclaringType(concrete);
		
		if (t instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)t).setDeclaringMember(f);
		}
		return f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConstructor() {
		return false;
	}
	
	public int hashCode(){
		return name.hashCode();
	}

	public boolean equals (Object other){
		return other instanceof Field && ((Field)other).name.equals(this.name);
	}

	@Override
	public boolean isIndexer() {
		return false;
	}
	
    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }
}
