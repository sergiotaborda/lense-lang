package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.TypeDefinition;

public class PrimitiveArithmeticOperationsNode extends ExpressionNode {

	private ArithmeticOperation operation;

	public PrimitiveArithmeticOperationsNode(TypeDefinition type, AstNode original, ArithmeticOperation operation){
		this.add(original);
		this.operation = operation;
		this.setTypeVariable(type);
	}
	
	public PrimitiveArithmeticOperationsNode(TypeDefinition type,AstNode first, AstNode second, ArithmeticOperation operation){
		this.add(first);
		this.add(second);
		this.operation = operation;
		this.setTypeVariable( type);
	}


	public ArithmeticOperation getOperation() {
		return operation;
	}

}
