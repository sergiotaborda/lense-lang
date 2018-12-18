package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.crosscompile.ErasurePointNode.ErasureOperation;

public class ElideErasureVisitor implements Visitor<AstNode> {

    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {
        return VisitorNext.Children;
    }

    @Override
    public void visitAfterChildren(AstNode node) {

        if (node instanceof ErasurePointNode){
            ErasurePointNode boxingPoint = (ErasurePointNode)node;
            ExpressionNode inner = boxingPoint.getValue();


            if (inner.getTypeVariable() == null){
                return;
            } 
            
            if (boxingPoint.getErasureOperation() == ErasureOperation.BOXING && boxingPoint.isBoxingDirectionOut()){
                // apply casting
                CastNode cast = new CastNode(inner, boxingPoint.getTypeVariable().getUpperBound());
                
                boxingPoint.getParent().replace(boxingPoint, cast);
                
            } else if (boxingPoint.canElide() && inner.getTypeVariable().equals(boxingPoint.getTypeVariable())){
                // elide boxing
                boxingPoint.getParent().replace(boxingPoint, inner);
            } 
//TODO remove conversions to Any
        }
    }






}
