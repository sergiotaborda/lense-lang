/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;




/**
 * 
 */
public class LiteralTupleInstanceCreation extends NewInstanceCreationNode implements LiteralCreation{

	public LiteralTupleInstanceCreation (AstNode value, AstNode nextTuple){
		super(new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple")), new ArgumentListNode(value, nextTuple));
	}

	public LiteralTupleInstanceCreation (AstNode value){
		super(new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple")), new ArgumentListNode(value));
	}


}
