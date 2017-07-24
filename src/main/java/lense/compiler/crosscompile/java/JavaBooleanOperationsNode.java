package lense.compiler.crosscompile.java;

import compiler.syntax.AstNode;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.FixedTypeVariable;

public class JavaBooleanOperationsNode extends ExpressionNode {

	private BooleanOperation operation;

	public JavaBooleanOperationsNode(AstNode original, BooleanOperatorNode.BooleanOperation operation){
		this.add(original);
		this.operation = operation;
		this.setTypeVariable(new FixedTypeVariable(new JavaPrimitiveTypeDefinition("boolean")));
	}

	public BooleanOperation getOperation() {
		return operation;
	}

}
