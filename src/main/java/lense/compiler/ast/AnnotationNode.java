/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class AnnotationNode extends LenseAstNode {

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
