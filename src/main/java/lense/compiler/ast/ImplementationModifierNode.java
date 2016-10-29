/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ImplementationModifierNode extends LenseAstNode {

	private boolean isAbstract;
	private boolean isNative;
	private boolean isSealed;
	
	/**
	 * Constructor.
	 * @param variant
	 */
	public ImplementationModifierNode() {

	}

	public boolean isAbstract(){
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public boolean isSealed() {
		return isSealed;
	}

	public void setSealed(boolean isSealed) {
		this.isSealed = isSealed;
	}

	public ImplementationModifierNode and(ImplementationModifierNode other) {
		ImplementationModifierNode n = new ImplementationModifierNode();
		
		n.isAbstract = this.isAbstract || other.isAbstract;
		n.isNative = this.isNative || other.isNative;
		n.isSealed = this.isSealed || other .isSealed;
		
		return n;
	}


}
