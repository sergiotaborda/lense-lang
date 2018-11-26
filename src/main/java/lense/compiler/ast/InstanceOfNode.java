package lense.compiler.ast;

import java.util.Optional;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class InstanceOfNode extends BooleanExpressionNode{

	private Boolean evaluation;

    public InstanceOfNode() {
	
	}

	public TypeNode getTypeNode(){
		return (TypeNode) this.getChildren().get(1);
	}

	public ExpressionNode getExpression() {
		return (ExpressionNode) this.getChildren().get(0);
	}

    public void setMandatoryEvaluation(Boolean evaluation) {
       this.evaluation = evaluation;
    }
    
    public Optional<Boolean> getMandatoryEvaluation(){
        return Optional.ofNullable(this.evaluation);
    }
    
    private TypeVariable type = LenseTypeSystem.Boolean();

    public TypeVariable getTypeVariable() {
        return type;
    }

    public void setTypeVariable(TypeVariable type) {
        this.type = type;
    }
}
