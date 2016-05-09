package lense.compiler.ast;

public class PropertyDeclarationNode extends InvocableDeclarionNode {

	private AccessorNode acessor;
	private ModifierNode modifier;
	private String name;
	private TypeNode type;
	private AnnotationListNode annotationListNode;
	
	public boolean isIndexed(){
		return false;
	}
	
	public void setAcessor(AccessorNode node) {
		acessor= node;
		this.add(node);
	}
	
	public void setModifier(ModifierNode node) {
		modifier = node;
		this.add(node);
	}

	public void setName(String name) {
		this.name= name;
	}

	public void setType(TypeNode typeNode) {
		type = typeNode;
		this.add(type);
		
	}

	public void setAnnotations(AnnotationListNode annotationListNode) {
		this.annotationListNode = annotationListNode;
		this.add(annotationListNode);
		
	}
	
	public AnnotationListNode getAnnotations() {
		return annotationListNode;
	}
	
	public AccessorNode getAcessor() {
		return acessor;
	}

	public ModifierNode getModifier() {
		return modifier;
	}
	
	public TypeNode getType() {
		return type;
	}
	
	public String getName () {
		return name;
	}

}
