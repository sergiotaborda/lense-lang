package lense.compiler.ast;

import lense.compiler.type.TypeKind;
import lense.compiler.type.variable.TypeVariable;

public class ReadThisType extends ExpressionNode {

	
	private TypeKind kind;

	public ReadThisType(TypeNode typeNode, TypeKind kind) {
		this.add(typeNode);
		this.kind = kind;
	}

	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(0);
	}

	public TypeVariable getTypeVariable() {
		return getTypeNode().getTypeVariable();
	}

	public TypeKind getKind() {
		return kind;
	}
}
