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
public class Property  implements TypeMember{

	private TypeVariable type;
	private String name;
	private TypeDefinition declaringType;
	private boolean canRead;
	private boolean canWrite;
    private Visibility visibility;
	 
	/**
	 * Constructor.
	 * @param typeAdapter
	 * @param name
	 * @param fromClass
	 */
	public Property(TypeDefinition declaringType, String name, TypeVariable type, boolean canRead, boolean canWrite ) {
		this.type = type;
		this.name = name;
		this.declaringType = declaringType;
		this.canRead=  canRead;
		this.canWrite =canWrite;
	}

	public String toString(){
		return name + "(property)";
	}
	
	public TypeVariable getReturningType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public TypeDefinition getDeclaringType() {
		return declaringType;
	}

	@Override
	public boolean isField() {
		return false;
	}

	@Override
	public boolean isProperty() {
		return true;
	}

	@Override
	public boolean isMethod() {
		return false;
	}

	@Override
	public boolean isConstructor() {
		return false;
	}

	@Override
	public TypeMember changeDeclaringType(TypeDefinition concrete) {
		TypeVariable t = this.type.changeBaseType(concrete);
		
		Property p = new Property(concrete, this.name, this.type.changeBaseType(concrete), this.canRead, this.canWrite);
		if (t instanceof TypeMemberAwareTypeVariable){
			((TypeMemberAwareTypeVariable)t).setDeclaringMember(p);
		}
		return p;
	}

	@Override
	public boolean isIndexer() {
		return false;
	}

	public void setWritable(boolean canWrite) {
		this.canWrite = canWrite;
	}
	
	public void setReadable(boolean canRead) {
		this.canRead = canRead;
	}

	public boolean canRead() {
		return canRead;
	}
	
	public boolean canWrite() {
		return canWrite;
	}

    @Override
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility){
        this.visibility = visibility;
    }
    
	public int hashCode(){
		return name.hashCode();
	}

	public boolean equals (Object other){
		return other instanceof Property && ((Property)other).name.equals(this.name);
	}

}
