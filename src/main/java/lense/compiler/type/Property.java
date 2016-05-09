/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeMemberAwareTypeVariable;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class Property  implements TypeMember{

	private TypeVariable type;
	private String name;
	private TypeDefinition declaringType;
	private boolean canRead;
	private boolean canWrite;
	 
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

	



}
