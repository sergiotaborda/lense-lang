/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.LenseAstNode;
import compiler.syntax.AstNode;




/**
 * 
 */
public class MethodCallNode extends LenseAstNode {

	private String name;
	private ArgumentListNode arguments;

	
	public MethodCallNode (){}
	/**
	 * Constructor.
	 * @param name2
	 * @param arguments2
	 */
	public MethodCallNode(String name, ArgumentListNode arguments) {
		this.name = name;
		setArgumentListNode(arguments);
	}
	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param argumentListNode
	 */
	public void setArgumentListNode(ArgumentListNode arguments) {
		this.arguments = arguments;
		this.add(arguments);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public ArgumentListNode getArgumentListNode(){
		return arguments;
	}
	
	public void replace(AstNode node, AstNode newnode){
		super.replace(node, newnode);
		
		if (this.arguments == node){
			this.arguments = (ArgumentListNode) newnode;
		}
	}

}
