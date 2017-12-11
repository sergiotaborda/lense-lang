/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class LiteralSequenceInstanceCreation extends NewInstanceCreationNode implements LiteralCreation{

	public LiteralSequenceInstanceCreation (ArgumentListNode list){
		super(new TypeNode(new QualifiedNameNode("lense.core.collections.Sequence")), list);
	}


}
