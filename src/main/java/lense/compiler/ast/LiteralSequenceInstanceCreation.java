/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class LiteralSequenceInstanceCreation extends ClassInstanceCreationNode implements LiteralCreation{

	public LiteralSequenceInstanceCreation (ArgumentListNode list){
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Sequence"));
		setTypeNode(t);
		setArguments(list);
	}


}
