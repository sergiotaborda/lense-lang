package lense.compiler.ast;

public class CaptureReifiedTypesNode extends LenseAstNode {

	
	private TypeParametersListNode typeParametersListNode = new TypeParametersListNode();
	
	public CaptureReifiedTypesNode(TypeParametersListNode typeParametersListNode) {
		this.typeParametersListNode = typeParametersListNode;
	}

	
	public TypeParametersListNode getTypeParametersListNode() {
		return typeParametersListNode;
	}
}
