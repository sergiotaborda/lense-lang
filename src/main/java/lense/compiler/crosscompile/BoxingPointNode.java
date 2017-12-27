package lense.compiler.crosscompile;

import java.util.function.Function;

import compiler.syntax.AstNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.type.variable.TypeVariable;

public class BoxingPointNode extends ExpressionNode {

	public enum BoxingDirection {
		BOXING_IN, // wrap primitive
		BOXING_OUT, // convert to primitive
	}
	
	private BoxingDirection boxingDirection;
	private AstNode referenceNode;
	private Function<AstNode, TypeVariable> read;
	private boolean canElide = true; // can be removed if the value is already of the expected type ?
	
	public BoxingPointNode(ExpressionNode expression, AstNode referenceNode, BoxingDirection boxingDirection){
		this.add(expression);
		this.referenceNode = referenceNode;
		this.boxingDirection = boxingDirection;
		this.read = (tn -> ((TypedNode)tn).getTypeVariable());
	}
	
	public BoxingPointNode(ExpressionNode expression, AstNode referenceNode, Function<AstNode, TypeVariable> read, BoxingDirection boxingDirection){
		this.add(expression);
		this.referenceNode = referenceNode;
		this.read = read;
		this.boxingDirection = boxingDirection;
	}

	public TypeVariable getTypeVariable() {
		return read.apply(referenceNode);
	}


	public ExpressionNode getValue() {
		return (ExpressionNode)this.getChildren().get(0);
	}

	public boolean isBoxingDirectionOut() {
		return boxingDirection == BoxingDirection.BOXING_OUT;
	}

	public AstNode getReferenceNode() {
		return referenceNode;
	}

	public boolean canElide() {
		return canElide;
	}

	public void setCanElide(boolean canElide) {
		this.canElide = canElide;
	}



}
