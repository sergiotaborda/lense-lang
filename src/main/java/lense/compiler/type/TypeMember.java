/**
 * 
 */
package lense.compiler.type;

import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public interface TypeMember {

	/**
	 * @return
	 */
	boolean isField();

	/**
	 * @return
	 */
	String getName();

	/**
	 * @return
	 */
	boolean isProperty();

	/**
	 * @return
	 */
	boolean isIndexer();
	
	/**
	 * @return
	 */
	boolean isMethod();
	
	/**
	 * @return
	 */
	boolean isConstructor();
	
	public TypeDefinition getDeclaringType();

	/**
	 * @param concrete
	 * @return
	 */
	TypeMember changeDeclaringType(TypeDefinition concrete);

	
	public Visibility getVisibility();

}
