/**
 * 
 */
package lense.compiler.crosscompile.java.ast;





/**
 * 
 */
public class MethodCallNode extends JavaAstNode {

	private String name;
	private ArgumentListNode arguments;

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

}
