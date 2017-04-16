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
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple"));
		setTypeNode(t);
		ArgumentListNode list = new ArgumentListNode();
		list.add(value);
		list.add(nextTuple);
		
		setArguments(list);
	}

	public LiteralTupleInstanceCreation (AstNode value){
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple"));
		setTypeNode(t);
		ArgumentListNode list = new ArgumentListNode();
		list.add(value);
		setArguments(list);
	}


}
