package lense.compiler.crosscompile.java;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class JavaPrimitiveBooleanUnbox extends ExpressionNode {

	public JavaPrimitiveBooleanUnbox(AstNode other) {
		this.add(other);
	}
	
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(new JavaPrimitiveTypeDefinition("boolean"));
	}

}
