/**
 * 
 */
package lense.compiler.crosscompile.java.ast;



/**
 * 
 */
public class AnnotationNode extends JavaAstNode {

	private String name;

	/**
	 * @param string
	 */
	public void setName(String name) {
		this.name= name;
	}
	
	public String getName(){
		return name;
	}

}
