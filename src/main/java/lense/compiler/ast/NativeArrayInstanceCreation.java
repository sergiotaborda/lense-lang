/**
 * 
 */
package lense.compiler.ast;




/**
 * 
 */
public class NativeArrayInstanceCreation extends ClassInstanceCreation{

	public NativeArrayInstanceCreation (ArgumentListNode list){
		TypeNode t = new TypeNode(new QualifiedNameNode("lense.core.collections.Sequence"));
		setTypeNode(t);
		setArguments(list);
	}


}
