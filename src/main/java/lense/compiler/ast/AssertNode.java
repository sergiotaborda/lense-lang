/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class AssertNode extends StatementNode {

    private boolean referenceValue = true;
    
    public AssertNode (ExpressionNode expressionNode){
        this.add(expressionNode);
    }

	public boolean getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(boolean referenceValue) {
		this.referenceValue = referenceValue;
	}
    
    
}
