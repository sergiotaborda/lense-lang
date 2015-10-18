/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.AnnotationListNode;
import lense.compiler.ast.LenseAstNode;


/**
 * 
 */
public class AnnotadedSenseAstNode extends LenseAstNode  {

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
