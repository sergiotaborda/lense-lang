package lense.compiler.crosscompile;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.crosscompile.ErasurePointNode.BoxingDirection;
import lense.compiler.crosscompile.ErasurePointNode.ErasureOperation;
import lense.compiler.phases.AbstractScopedVisitor;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class BooleanErasureVisitor extends AbstractScopedVisitor {

    private static final PrimitiveTypeDefinition primitiveType = PrimitiveTypeDefinition.BOOLEAN;
    private static final TypeDefinition type = LenseTypeSystem.Boolean();
    private static final ErasedTypeDefinition erasedType = new ErasedTypeDefinition( type,  primitiveType);
    private LenseTypeDefinition currentType;

    public BooleanErasureVisitor(SemanticContext context) {
        super(context);
    }

    @Override
    protected Optional<LenseTypeDefinition> getCurrentType() {

        return Optional.ofNullable(currentType);
    }

    @Override
    public VisitorNext doVisitBeforeChildren(AstNode node) {
        if (node instanceof ClassTypeNode) {

            this.currentType = ((ClassTypeNode) node).getTypeDefinition();

        } else if (node instanceof InstanceOfNode){
            ((InstanceOfNode) node).setTypeVariable(primitiveType);
        } else if (node instanceof ReturnNode){
            ReturnNode r = (ReturnNode)node;

            TypeVariable tv = r.getExpectedType();

            if (tv != null && tv.isFixed() &&  LenseTypeSystem.isAssignableTo(tv, type)) {
                r.setExpectedType(erasedType);
                if (r.getFirstChild() instanceof ErasurePointNode){
                    ErasurePointNode p =(ErasurePointNode)r.getFirstChild();
                    p.setTypeVariable(erasedType);
                }
            } 

        } else if (node instanceof BooleanOperatorNode){
            BooleanOperatorNode op = ((BooleanOperatorNode)node);
            
            final ExpressionNode right = op.getRight();
            if (right instanceof ErasurePointNode){
                ErasurePointNode p = (ErasurePointNode)right;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    op.replace(p, p.getFirstChild());
                }
            }
            
            final ExpressionNode left = op.getLeft();
            if (left instanceof ErasurePointNode){
                ErasurePointNode p = (ErasurePointNode)left;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    op.replace(p, p.getFirstChild());
                }
            }
 
            op.setTypeVariable(erasedType);
            
        } else if (node instanceof DecisionNode){
            
            final ExpressionNode condition =  ((DecisionNode) node).getCondition();
            if (condition instanceof ErasurePointNode){
                ErasurePointNode p = (ErasurePointNode)condition;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    node.replace(p, p.getFirstChild());
                }
            }
            
        } else if (node instanceof TypedNode && !(node instanceof ErasurePointNode )) {
            TypedNode t = (TypedNode) node;

            TypeVariable tv = t.getTypeVariable();

            if (tv != null && tv.isFixed() && !isTupleAccess(node) && LenseTypeSystem.isAssignableTo(tv, type)) {
                t.setTypeVariable(erasedType);
            } 

        } 
        
        return VisitorNext.Children;
    }

    private boolean isTupleAccess(AstNode node) {
        if (node instanceof CastNode) {
            return ((CastNode) node).isTupleAccessMethod();
        } else if (node instanceof MethodInvocationNode) {
            return ((MethodInvocationNode) node).isTupleAccessMethod();
        }
        return false;
    }

    @Override
    public void doVisitAfterChildren(AstNode node) {

        if (node instanceof AssertNode){
            AssertNode a = (AssertNode)node;

            if (!a.getCheck().getTypeVariable().equals(primitiveType)){

                a.replace(a.getCheck(), new PrimitiveUnbox(primitiveType, a.getCheck()));
            }

        } else if (node instanceof MethodInvocationNode) {
            MethodInvocationNode m = (MethodInvocationNode) node;

           // TypedNode access = (TypedNode)m.getAccess();
           // if (access != null  && access.getTypeVariable() != null && access.getTypeVariable().getTypeDefinition().equals(primitiveType)) {

                if (m.getCall().getName().equals("negate")){

                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
                } else if (m.getCall().getName().equals("or")){

                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitOr));
                } else if (m.getCall().getName().equals("and")){

                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitAnd));
                } else if (m.getCall().getName().equals("xor")){

                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitXor));
                } else if (m.getCall().getName().equals("equalsTo")){

                    // if equals to another boolean
                    AstNode val = m.getCall().getArguments().getFirstArgument().getFirstChild();
                    if ( val instanceof BooleanValue) {
                        // remove call to equals method
                        m.getParent().replace(m, m.getAccess());

                        BooleanValue bool = (BooleanValue)val;
                        if (!bool.isValue()) {
                            // remove call to equals method and negate
                            m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
                        }
                    }

                }
//            } else if (access != null  && access.getTypeVariable() != null && access.getTypeVariable().getTypeDefinition().equals(type)){
//                CastNode cast = new CastNode((LenseAstNode) access , type);
//                m.replace((AstNode) access, cast);
//            } 
//            else if (access != null  &&  access.getTypeVariable() != null //&& access.getTypeVariable().equals(currentType)
//                    && m.getCall().getName().equals("bitAt")){
//
//                if (m.getTypeVariable().getTypeDefinition().equals(primitiveType) && ((Method)m.getTypeMember()).getReturningType().equals(type)){
//                    // was errased 
//                    System.out.println("Errased: "+  m.getParent().getParent());
//
//                    if (m.getParent() instanceof ErasurePointNode){
//                        ErasurePointNode box = (ErasurePointNode)m.getParent();
//                    }
//                }
//
//
//            }
        } else if (node instanceof ErasurePointNode){
            ErasurePointNode boxingPoint = (ErasurePointNode)node;
            ExpressionNode inner = boxingPoint.getValue();

            TypeVariable originalType = inner.getTypeVariable();
            TypeVariable targetType = boxingPoint.getTypeVariable();


//            if (originalType == null){
//                return;
//            } 
//
//            if (boxingPoint.canElide() && originalType.equals(targetType)){
//                return;
//            } 


            if (boxingPoint.getErasureOperation() == ErasureOperation.CONVERTION){
                // CONVERTION

                if (inner instanceof PrimitiveBox && primitiveType.equals(targetType)){
                    boxingPoint.getParent().replace(boxingPoint, inner.getFirstChild() );
                
                } else if (type.equals(originalType) && primitiveType.equals(targetType)){
                    // convert to primitive by unboxing

                    if (inner instanceof BooleanValue){
                        // is a literal
                        boxingPoint.getParent().replace(boxingPoint, new PrimitiveBooleanValue(((BooleanValue)inner).isValue()));
                    } else {
                        boxingPoint.getParent().replace(boxingPoint, new PrimitiveUnbox(primitiveType, inner));
                    }

                } else if (primitiveType.equals(originalType) && type.equals(targetType)){
                    // convert to object type by boxing
                   boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(erasedType, inner));
                } else if (( type.equals(targetType) || primitiveType.equals(targetType)) && targetType.equals(originalType)){
                    // the types are expected and already are the same, remove erasure point
                    boxingPoint.getParent().replace(boxingPoint, inner);
                } else if (boxingPoint.getFirstChild() instanceof ErasurePointNode){
                    ErasurePointNode p = (ErasurePointNode)boxingPoint.getFirstChild();
                    
                    if (p.getTypeVariable().equals(type) || p.getTypeVariable().equals(primitiveType)){
                        if (p.getBoxingDirection() == BoxingDirection.BOXING_OUT){
                          
                       }
                       
                    } 
//                    else if (p.getBoxingDirection() == BoxingDirection.BOXING_OUT){
//                        
//                    }
                } // else, some other type not interest in

            } else if (boxingPoint.getBoxingDirection() == BoxingDirection.BOXING_IN && targetType.equals(type)){
                // BOXING IN
                if (inner instanceof BooleanValue){
                    // a literal is already boxed
                    boxingPoint.getParent().replace(boxingPoint, inner); 
                } else if (primitiveType.equals(originalType)){
                    // the original type is a primitive

                    if ((inner instanceof VariableReadNode && boxingPoint.canElide()) || inner instanceof PrimitiveBooleanOperationsNode) {
                        // if can be elided, does not box , simple remove the box arround inner
                        boxingPoint.getParent().replace(boxingPoint, inner);
                    } else {
                        // box the primitive
                        boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(erasedType, inner));
                    }
                } // else, some other type not interest in

            } else if (boxingPoint.getBoxingDirection() == BoxingDirection.BOXING_OUT) {
                // BOXING OUT
                if ( inner instanceof BooleanValue) {
                    //	                  if(boxingPoint.getReferenceNode() instanceof ArgumentListItemNode){
                    //	                      ArgumentListItemNode ref = (ArgumentListItemNode)boxingPoint.getReferenceNode();
                    //	                      if (ref.isGeneric()){
                    //	                          boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(primitiveType, inner));
                    //	                          return;
                    //	                      }
                    //	                  } 

                    boxingPoint.getParent().replace(boxingPoint, new PrimitiveBooleanValue(((BooleanValue)inner).isValue()));
                } else if (inner instanceof PrimitiveBox || inner instanceof PrimitiveBooleanValue) {
                 
                    if (inner.getTypeVariable().equals(primitiveType)) {
                        // unbox the box is the same as do nothing
                        
                        boxingPoint.getParent().replace(boxingPoint, inner.getFirstChild());
                    }
                } else if (boxingPoint.getTypeVariable() != null && !originalType.isFixed() && targetType.getTypeDefinition().equals(primitiveType)){

                    boxingPoint.getParent().replace(boxingPoint, new PrimitiveUnbox(primitiveType, inner));


                } // else, some other type not interest in

            }

        }

    }


}
