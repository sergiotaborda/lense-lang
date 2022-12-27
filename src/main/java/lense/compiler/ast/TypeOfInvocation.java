/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class TypeOfInvocation extends ExpressionNode  {

	
	public TypeOfInvocation(AstNode astNode){
		this.add(astNode);
	}
	
	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(0);
	}

	public TypeVariable getTypeVariable() {
		return getTypeNode().getTypeVariable();
	}
	
	
	public String toString() {
		return "typeOf(" + getTypeVariable().toString() + ")";
	}
}
