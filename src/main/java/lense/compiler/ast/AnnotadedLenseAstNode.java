/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public abstract class AnnotadedLenseAstNode extends LenseAstNode  {

	private AnnotationListNode annotations;
	
	/**
	 * {@inheritDoc}
	 */
	public void setAnnotations(AnnotationListNode annotations) {
		this.annotations = annotations;
		this.add(annotations);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public AnnotationListNode getAnnotations(){
		return annotations;
	}
}
