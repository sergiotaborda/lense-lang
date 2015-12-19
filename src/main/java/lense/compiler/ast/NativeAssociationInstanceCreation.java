/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class NativeAssociationInstanceCreation extends ClassInstanceCreation{

	public NativeAssociationInstanceCreation (ArgumentListNode list){
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Association"));
		setTypeNode(t);
		setArguments(list);
	}


}
