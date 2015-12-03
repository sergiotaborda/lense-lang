/**
 * 
 */
package lense.compiler.ast;

import java.util.stream.Collectors;

import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.TypedNode;
import compiler.syntax.AstNode;
import compiler.typesystem.MethodParameter;


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
