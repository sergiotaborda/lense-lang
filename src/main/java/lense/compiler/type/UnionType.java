/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class UnionType extends LenseTypeDefinition {

	private TypeVariable left;
	private TypeVariable right;
	
	/**
	 * Constructor.
	 * @param name
	 */
	public UnionType(TypeVariable left, TypeVariable right) {
		super(left.toString() + "|" + right.toString(), LenseUnitKind.Class, null);
		this.left = left;
		this.right = right;
	}
	
	public TypeVariable getLeft(){
		return left;
	}
	
	public TypeVariable getRight(){
		return right;
	}
}
