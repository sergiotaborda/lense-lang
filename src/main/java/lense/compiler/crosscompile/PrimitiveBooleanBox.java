package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveBooleanBox extends ExpressionNode {

	public PrimitiveBooleanBox(AstNode other) {
		this.add(other);
	}
	
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(new PrimitiveTypeDefinition("boolean"));
	}

}