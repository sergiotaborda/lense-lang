/**
 * 
 */
package lense.compiler.ast;

import java.util.Optional;

/**
 * 
 */
public class AssertNode extends StatementNode {

    private boolean referenceValue = true;
    
    public AssertNode (ExpressionNode expressionNode){
        this.add(expressionNode);
    }

    public ExpressionNode getCheck() {
    	return (ExpressionNode) this.getFirstChild();
    }
    
    public Optional<ExpressionNode> getText() {
    	return this.getChildren().size() == 2 ? Optional.of((ExpressionNode)this.getChildren().get(1)) : Optional.empty() ;
    }
    
    public void setText(ExpressionNode text) {
    	if (this.getChildren().size() == 2) {
    		this.getChildren().set(1, text);
    	} else {
    		this.add(text);
    	}
    }
    
	public boolean getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(boolean referenceValue) {
		this.referenceValue = referenceValue;
	}
    
    
}
