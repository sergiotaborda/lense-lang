package lense.compiler.ast;

public class CaptureReifiedTypesNode extends LenseAstNode {

	
	private TypeParametersListNode typeParametersListNode = new TypeParametersListNode();
	private ReificationScope scope;
	public int count;
	
	public CaptureReifiedTypesNode(ReificationScope scope, TypeParametersListNode typeParametersListNode) {
		this.typeParametersListNode = typeParametersListNode;
		this.scope = scope;
	}

	public ReificationScope getReificationScope() {
		return scope;
	}
	
	public TypeParametersListNode getTypeParametersListNode() {
		return typeParametersListNode;
	}
}
