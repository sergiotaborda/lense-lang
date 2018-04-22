package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.ExpressionNode;

public class PrimitiveBooleanOperationsNode extends ExpressionNode {

	private BooleanOperation operation;

	public PrimitiveBooleanOperationsNode(AstNode original, BooleanOperation operation){
		this.add(original);
		this.operation = operation;
		this.setTypeVariable( PrimitiveTypeDefinition.BOOLEAN);
	}
	
	public PrimitiveBooleanOperationsNode(AstNode first, AstNode second, BooleanOperation operation){
		this.add(first);
		this.add(second);
		this.operation = operation;
		this.setTypeVariable( PrimitiveTypeDefinition.BOOLEAN);
	}


	public BooleanOperation getOperation() {
		return operation;
	}

}
