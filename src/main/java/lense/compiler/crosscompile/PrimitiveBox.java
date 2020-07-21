package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveBox extends ExpressionNode {

    private TypeDefinition type;
    
	public PrimitiveBox(TypeDefinition type, AstNode other) {
		this.add(other);
		this.type = type;
	}
	
	public TypeVariable getTypeVariable() {
		return type;
	}

}
