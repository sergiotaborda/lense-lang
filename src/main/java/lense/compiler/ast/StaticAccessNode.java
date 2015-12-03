/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class StaticAccessNode extends LenseAstNode {

	
	private TypeNode type;
	
	public StaticAccessNode(TypeNode type){
		this.type = type;
		this.add(type);
	}
	
	public TypeNode getType(){
		return type;
	}
}
