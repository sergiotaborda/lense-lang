/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class VisibilityNode extends LenseAstNode {

	private Visibility visibility;
	
	/**
	 * Constructor.
	 * @param variant
	 */
	public VisibilityNode(Visibility visibility) {
		this.visibility = visibility;
	}
	
	public VisibilityNode() {
		this.visibility = Visibility.Undefined;
	}

	public Visibility getVisibility() {
		return visibility;
	}
	
	public Visibility getVisibility(Visibility other) {
		return visibility == Visibility.Undefined ? other : visibility;
	}

}
