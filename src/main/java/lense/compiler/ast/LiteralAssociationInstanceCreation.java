/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class LiteralAssociationInstanceCreation extends ClassInstanceCreationNode implements LiteralCreation{

	public LiteralAssociationInstanceCreation (ArgumentListNode list){
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Association"));
		setTypeNode(t);
		setArguments(list);
	}


}
