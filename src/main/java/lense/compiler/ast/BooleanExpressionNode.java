/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.typesystem.LenseTypeSystem;

/**
 * 
 */
public abstract class BooleanExpressionNode extends ExpressionNode {

    public BooleanExpressionNode (){
        this.setTypeVariable(LenseTypeSystem.Boolean());
    }
    

}
