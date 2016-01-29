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
	
	public ArgumentListNode (AstNode... params){
		for(AstNode n : params){
			this.add(n);
		}
	}

}
