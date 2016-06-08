package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class CastNode extends ExpressionNode {

	
	private TypeDefinition type;

	public CastNode (LenseAstNode other, TypeDefinition type){
		this.add(other);
		this.type = type;
	}

	public LenseAstNode getInner(){
		return (LenseAstNode) this.getChildren().get(0);
	}

	public TypeDefinition getType() {
		return type;
	}

	public TypeVariable getTypeVariable() {
		return new  FixedTypeVariable(type);
	}
}

