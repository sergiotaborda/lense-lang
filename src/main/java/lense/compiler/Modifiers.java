package lense.compiler;

import lense.compiler.ast.AnnotationListNode;
import lense.compiler.ast.ImplementationModifierNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.typesystem.Imutability;
public class Modifiers {

	private AnnotationListNode annotations = null;
	private VisibilityNode visibility = new VisibilityNode();
	private ImplementationModifierNode implementationModifier = new ImplementationModifierNode();
	private ImutabilityNode imutability = new ImutabilityNode(Imutability.Imutable);
	
	public AnnotationListNode getAnnotations() {
		return annotations;
	}
	public void setAnnotations(AnnotationListNode annotations) {
		this.annotations = annotations;
	}
	public VisibilityNode getVisibility() {
		return visibility;
	}
	public void setVisibility(VisibilityNode visibility) {
		this.visibility = visibility;
	}
	public ImplementationModifierNode getImplementationModifier() {
		return implementationModifier;
	}
	public void setImplementationModifier(ImplementationModifierNode implementationModifier) {
		this.implementationModifier = implementationModifier;
	}
	public void setImutability(ImutabilityNode imutability) {
		this.imutability = imutability;
	}
	public ImutabilityNode getImutability() {
		return this.imutability;
	}
}
