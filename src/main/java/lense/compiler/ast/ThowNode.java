/**
 * 
 */
package lense.compiler.ast;

/**
 * 
 */
public class ThowNode extends StatementNode {

    
    public ThowNode (ExpressionNode expressionNode){
        this.add(expressionNode);
    }
}
