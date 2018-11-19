package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveUnbox extends ExpressionNode {

    private PrimitiveTypeDefinition type;
	public PrimitiveUnbox(PrimitiveTypeDefinition type, AstNode other) {
		this.add(other);
		this.type = type;
	}
	
	public TypeVariable getTypeVariable() {
		return type;
	}

}
