/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class AssertNode extends StatementNode {

    
    public AssertNode (ExpressionNode expressionNode){
        this.add(expressionNode);
    }
}
