/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;




/**
 * 
 */
public class MethodCallNode extends StatementNode {

	private String name;
	private ArgumentListNode arguments;

	
	public MethodCallNode (String name){
		if (name == null){
			throw new IllegalArgumentException();
		}
		this.name = name;
	}
	/**
	 * Constructor.
	 * @param name2
	 * @param arguments2
	 */
	public MethodCallNode(String name, ArgumentListNode arguments) {
		this(name);
		setArgumentListNode(arguments);
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
