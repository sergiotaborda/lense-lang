package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;

public class CastNode extends ExpressionNode {

	
	private TypeVariable type;

	public CastNode (LenseAstNode other, TypeVariable type){
	    if (type == null){
            throw new IllegalArgumentException("Type is necessary");
        }
		this.add(other);
		this.type = type;
	}

	public LenseAstNode getInner(){
		return (LenseAstNode) this.getChildren().get(0);
	}



	public TypeVariable getTypeVariable() {
		return type;
	}
}

