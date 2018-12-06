package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ExpressionNode;

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

            if (boxingPoint.canElide() && inner.getTypeVariable().equals(boxingPoint.getTypeVariable())){
                boxingPoint.getParent().replace(boxingPoint, inner);
            } 

        }
    }






}
