package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.typesystem.LenseTypeSystem;

public class PrimitiveBooleanOperationsNode extends ExpressionNode {

    private static ErasedTypeDefinition erased = new ErasedTypeDefinition(LenseTypeSystem.Boolean(),  PrimitiveTypeDefinition.BOOLEAN);
    
	private BooleanOperation operation;

	public PrimitiveBooleanOperationsNode(AstNode original, BooleanOperation operation){
		this.add(original);
		this.operation = operation;
		this.setTypeVariable(erased);
	}
	
	public PrimitiveBooleanOperationsNode(AstNode first, AstNode second, BooleanOperation operation){
		this(first, operation);
		this.add(second);
	}


	public BooleanOperation getOperation() {
		return operation;
	}

}
