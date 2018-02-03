package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveBooleanUnbox extends ExpressionNode {

	public PrimitiveBooleanUnbox(AstNode other) {
		this.add(other);
	}
	
	public TypeVariable getTypeVariable() {
		return PrimitiveTypeDefinition.BOOLEAN;
	}

}
