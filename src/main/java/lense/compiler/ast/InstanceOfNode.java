package lense.compiler.ast;

public class InstanceOfNode extends BooleanExpressionNode{

	public InstanceOfNode() {
	
	}

	
	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(1);
	}


	public ExpressionNode getExpression() {
		return (ExpressionNode) this.getChildren().get(0);
	}
}
