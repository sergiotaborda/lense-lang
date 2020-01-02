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
	private boolean isFinal;
	private boolean isDefault;
	private boolean isOverride;
	private boolean isValueClass;
	
	/**
	 * Constructor.
	 * @param variant
	 */
	public ImplementationModifierNode() {

	}

	public ImplementationModifierNode merge(ImplementationModifierNode other) {
		ImplementationModifierNode n = new ImplementationModifierNode();
		
		n.isAbstract = this.isAbstract || other.isAbstract;
		n.isNative = this.isNative || other.isNative;
		n.isSealed = this.isSealed || other.isSealed;
		n.isFinal = this.isFinal || other.isFinal;
		n.isDefault = this.isDefault || other.isDefault;
		n.isOverride = this.isOverride || other.isOverride;
		n.isValueClass = this.isValueClass || other.isValueClass;
		
		return n;
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

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isOverride() {
		return isOverride;
	}

	public void setOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}

	
	public boolean isValueClass() {
		return isValueClass;
	}
	

	public void setValueClass(boolean isValueClass) {
		this.isValueClass = isValueClass;
	}


}
