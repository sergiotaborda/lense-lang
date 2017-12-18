package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.crosscompile.java.JavaTypeKind;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class BoxingPointErasureVisitor implements Visitor<AstNode> {

    static FixedTypeVariable primitiveBooleanType = new FixedTypeVariable(new PrimitiveTypeDefinition("boolean"));

    @Override
    public void startVisit() {}

    @Override
    public void endVisit() {}

    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {

        if (node instanceof ReturnNode){
            ReturnNode r = (ReturnNode)node;

            TypeVariable tv = r.getExpectedType();

            if (tv != null && tv.isFixed() &&  LenseTypeSystem.Boolean().getName().equals(tv.getTypeDefinition().getName())) {


                r.setExpectedType(primitiveBooleanType);

            }
        } else if (node instanceof TypedNode && !(node instanceof BoxingPointNode )) {
            TypedNode t = (TypedNode) node;

            TypeVariable tv = t.getTypeVariable();

            if (tv != null && tv.isFixed() &&  LenseTypeSystem.Boolean().getName().equals(tv.getTypeDefinition().getName())) {

                FixedTypeVariable f = new FixedTypeVariable(new PrimitiveTypeDefinition("boolean"));
                t.setTypeVariable(f);
            }

        }  
        return VisitorNext.Children;
    }

    @Override
    public void visitAfterChildren(AstNode node) {

        if (node instanceof MethodInvocationNode) {
            MethodInvocationNode m = (MethodInvocationNode) node;

            TypedNode access = (TypedNode)m.getAccess();
            if (access != null && access.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive) {

                if (m.getCall().getName().equals("negate")){

                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
                }
            }
        } else if (node instanceof BoxingPointNode){
            BoxingPointNode a = (BoxingPointNode)node;
            ExpressionNode val = a.getValue();

            if (val.getTypeVariable() == null){
                return;
            } else
                if (val.getTypeVariable().equals(a.getTypeVariable())){
                    a.getParent().replace(a, val);
                } else {
                    if (a.isBoxingDirectionOut()){
                        // OUT BOXING

                        if (a.getTypeVariable() == null){

                            if (val instanceof BooleanValue){
                                // constant
                                if(a.getReferenceNode() instanceof ArgumentListItemNode){
                                    ArgumentListItemNode ref = (ArgumentListItemNode)a.getReferenceNode();
                                    if (ref.isGeneric()){
                                        a.getParent().replace(a, new PrimitiveBooleanBox(val));
                                        return;
                                    }
                                } 

                                a.getParent().replace(a, new PrimitiveBooleanValue(((BooleanValue)val).isValue()));
                            } 

                        } else if ( val instanceof BooleanValue || a.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive){

                            if (val instanceof BooleanValue){
                                // constant
                                if(a.getReferenceNode() instanceof ArgumentListItemNode){
                                    ArgumentListItemNode ref = (ArgumentListItemNode)a.getReferenceNode();
                                    if (ref.isGeneric()){
                                        a.getParent().replace(a, new PrimitiveBooleanBox(val));
                                        return;
                                    }
                                } 

                                a.getParent().replace(a, new PrimitiveBooleanValue(((BooleanValue)val).isValue()));
                            } else if (!val.getTypeVariable().isFixed()){

                                a.getParent().replace(a, new PrimitiveBooleanUnbox(val));
                            }

                        } 

                        // TODO StringConcatenationNode, StringValue

                    } else {
                        // IN BOXING
                        if (val instanceof BooleanValue){
                            a.getParent().replace(a, val);
                        } else if (val instanceof NumericValue){
                            NumericValue n = (NumericValue)val;
                            n.setTypeVariable(a.getTypeVariable());
                            a.getParent().replace(a, val);
                        } else if (val.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive){
                            a.getParent().replace(a, new PrimitiveBooleanBox(val));
                        }

                    }

                }




        }

    }

}
