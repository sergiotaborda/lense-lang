/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;


/**
 * 
 */
public class ArgumentListNode extends LenseAstNode {

	public ArgumentListNode (){}
	
	public static ArgumentListNode of(ArgumentListItemNode ... args) {
		return new ArgumentListNode(args);
	}
	
	public ArgumentListNode (AstNode... params){
		for(AstNode n : params){
			this.add(n);
		}
	}

	public int indexOf(AstNode node) {
		return this.getChildren().indexOf(node);
	}

	protected AstNode prepareAttach(AstNode node){
		
		if (node instanceof ArgumentListItemNode){
			return super.prepareAttach(node);
		} else {
			return super.prepareAttach(new ArgumentListItemNode(super.getChildren().size(), node));
		}
	
	}
	
	
	public ArgumentListItemNode getFirst(){

		return (ArgumentListItemNode)super.getFirstChild();
	}
	


}
