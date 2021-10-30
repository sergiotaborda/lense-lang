package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;

public class ReadTypeByNameNode extends ExpressionNode {

	private String typeName;

	public ReadTypeByNameNode(TypeNode type, String typeName){
		this.add(type);
	    this.typeName = typeName;
	}
	
	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(0);
	}

	public TypeVariable getTypeVariable() {
		return getTypeNode().getTypeVariable();
	}

	public String getTypeName() {
		return typeName;
	}
}
