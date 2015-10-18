/**
 * 
 */
package lense.compiler.typesystem;

import lense.compiler.typesystem.Kind;
import lense.compiler.typesystem.LenseTypeDefinition;
import compiler.typesystem.TypeDefinition;


/**
 * 
 */
public class UnionType extends LenseTypeDefinition {

	private TypeDefinition left;
	private TypeDefinition right;
	
	/**
	 * Constructor.
	 * @param name
	 */
	public UnionType(TypeDefinition left, TypeDefinition right) {
		super(left.toString() + "|" + right.toString(), Kind.Class, null);
		this.left = left;
		this.right = right;
	}
	
	public TypeDefinition getLeft(){
		return left;
	}
	
	public TypeDefinition getRight(){
		return right;
	}
}
