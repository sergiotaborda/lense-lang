package lense.compiler.crosscompile.java;

import java.util.function.Function;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.type.variable.TypeVariable;

public class BoxingPointNode extends ExpressionNode {

	
	
	private boolean boxingDirectionOut;
	private AstNode referenceNode;
	private Function<AstNode, TypeVariable> read;

	public BoxingPointNode(ExpressionNode expression, AstNode referenceNode, boolean boxingDirectionOut){
		this.add(expression);
		this.referenceNode = referenceNode;
		this.boxingDirectionOut = boxingDirectionOut;
		this.read = (tn -> ((TypedNode)tn).getTypeVariable());
	}
	
	public BoxingPointNode(ExpressionNode expression, AstNode referenceNode, Function<AstNode, TypeVariable> read, boolean boxingDirectionOut){
		this.add(expression);
		this.referenceNode = referenceNode;
		this.read = read;
		this.boxingDirectionOut = boxingDirectionOut;
	}

	public TypeVariable getTypeVariable() {
		return read.apply(referenceNode);
	}


	public ExpressionNode getValue() {
		return (ExpressionNode)this.getChildren().get(0);
	}

	public boolean isboxingDirectionOut() {
		return boxingDirectionOut;
	}

	public AstNode getReferenceNode() {
		return referenceNode;
	}



}
