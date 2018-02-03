package lense.compiler.ast;

import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class CastNode extends ExpressionNode {

	
	private TypeDefinition type;

	public CastNode (LenseAstNode other, TypeDefinition type){
	    if (type == null){
            throw new IllegalArgumentException("Type is necessary");
        }
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
		return type;
	}
}

