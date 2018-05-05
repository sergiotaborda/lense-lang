/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ChildTypeNode extends LenseAstNode{

	
	private TypeNode type;

	public ChildTypeNode(TypeNode type) {
		this.type = type;
	}

	public TypeNode getType() {
		return type;
	}

	
}
