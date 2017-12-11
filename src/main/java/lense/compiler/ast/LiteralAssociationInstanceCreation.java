/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class LiteralAssociationInstanceCreation extends NewInstanceCreationNode implements LiteralCreation{

	public LiteralAssociationInstanceCreation (ArgumentListNode list){
		super(new TypeNode(new QualifiedNameNode("lense.core.collections.Association")), list);
	}


}
