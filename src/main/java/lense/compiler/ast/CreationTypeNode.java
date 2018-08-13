package lense.compiler.ast;

public class CreationTypeNode extends LenseAstNode {

	private String typeName;
	private TypeParametersListNode typeParametersListNode = new TypeParametersListNode();
	
	public CreationTypeNode(String typeName) {
		this.typeName = typeName;
	}
	
	public void setParameters(TypeParametersListNode typeParametersListNode) {
		this.typeParametersListNode = typeParametersListNode;
	}

	
	public String getName() {
		return typeName;
	}

	public TypeParametersListNode getTypeParametersListNode() {
		return typeParametersListNode;
	}
}
