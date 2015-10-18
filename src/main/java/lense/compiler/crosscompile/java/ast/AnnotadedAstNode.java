/**
 * 
 */
package lense.compiler.crosscompile.java.ast;



/**
 * 
 */
public class AnnotadedAstNode extends JavaAstNode {

	private AnnotationListNode annotations;
	
	/**
	 * @param annotationListNode
	 */
	public void setAnnotations(AnnotationListNode annotations) {
		this.annotations = annotations;
		this.add(annotations);
	}
	
	public AnnotationListNode getAnnotations(){
		return annotations;
	}
}
