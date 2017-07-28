package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanOperatorNode.BooleanOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.variable.FixedTypeVariable;

public class PrimitiveBooleanOperationsNode extends ExpressionNode {

	private BooleanOperation operation;

	public PrimitiveBooleanOperationsNode(AstNode original, BooleanOperatorNode.BooleanOperation operation){
		this.add(original);
		this.operation = operation;
		this.setTypeVariable(new FixedTypeVariable(new PrimitiveTypeDefinition("boolean")));
	}

	public BooleanOperation getOperation() {
		return operation;
	}

}
