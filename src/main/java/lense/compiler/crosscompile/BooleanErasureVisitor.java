package lense.compiler.crosscompile;

import java.util.List;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.crosscompile.ErasurePointNode.BoxingDirection;
import lense.compiler.crosscompile.ErasurePointNode.ErasureOperation;
import lense.compiler.phases.AbstractScopedVisitor;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.TypeMember;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class BooleanErasureVisitor extends AbstractScopedVisitor {

    private static final PrimitiveTypeDefinition primitiveType = PrimitiveTypeDefinition.BOOLEAN;
    private static final TypeDefinition type = LenseTypeSystem.Boolean();
    private static final ErasedTypeDefinition erasedType = new ErasedTypeDefinition( type,  primitiveType);
    private static final TypeDefinition any = LenseTypeSystem.Any();

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
        }  else if (node instanceof MethodInvocationNode){

            MethodInvocationNode m = (MethodInvocationNode)node;
            TypeMember indexer = m.getTypeMember();

            if (m.isIndexDerivedMethod() && indexer instanceof IndexerProperty){

                TypeVariable parameterType = ((IndexerProperty)indexer).getReturningType();

                ArgumentListItemNode n  = m.getCall().getArguments().getLastArgument();

                AstNode arg = n.getFirstChild();
                TypeVariable argType = ((TypedNode)arg).getTypeVariable();

                if (arg instanceof ErasurePointNode && argType.equals(type)){
                    
                    if (parameterType.isFixed() && parameterType.equals(type)){
                        arg.getParent().replace(arg, arg.getFirstChild());
                    } else {
                        ErasurePointNode p = ErasurePointNode.box((ExpressionNode) arg.getFirstChild(), type);
                        p.setCanElide(false);
                        
                        arg.getParent().replace(arg, p);
                    }
                }
                

            }

        }  else if (node instanceof PreBooleanUnaryExpression){

            final ExpressionNode condition =  (ExpressionNode)((PreBooleanUnaryExpression) node).getFirstChild();
            if (condition instanceof ErasurePointNode && node.getParent() instanceof ConditionalStatement){
                ErasurePointNode p = (ErasurePointNode)condition;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    node.replace(p, p.getFirstChild());
                }
            }
        }  else if (node instanceof ConditionalStatement){

            final ExpressionNode condition =  ((ConditionalStatement) node).getCondition();
            if (condition instanceof ErasurePointNode){
                ErasurePointNode p = (ErasurePointNode)condition;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    node.replace(p, p.getFirstChild());
                }
            }
        } else if (node instanceof TypedNode
                && !(node instanceof ErasurePointNode )
                && !(node.getParent() instanceof VariableDeclarationNode )
                && !(node.getParent() instanceof InstanceOfNode)
                && !(node.getParent() instanceof GenericTypeParameterNode )
                && !(node instanceof GenericTypeParameterNode )
                ) {
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

            if (m.getCall().getName().equals("equalsTo")){

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

            } else if (m.getCall().getName().equals("negate")){

                if (m.getAccess() instanceof ErasurePointNode){
                    ErasurePointNode accessPoint  = (ErasurePointNode) m.getAccess();
                    if (primitiveType.equals(accessPoint.getTypeVariable())){
                        m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
                    }
                } else {
                    m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
                }
            } else {
                Optional<BooleanOperation> op = recognizeBooleanOperation(m.getCall().getName());

                if (op.isPresent()){
                    if (m.getAccess() instanceof ErasurePointNode){
                        ErasurePointNode accessPoint  = (ErasurePointNode) m.getAccess();
                        if (primitiveType.equals(accessPoint.getTypeVariable())){
                            m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), op.get()));
                        }
                    } else if (m.getAccess() instanceof PrimitiveBox && m.getCall().getArguments().getFirstArgument().getFirstChild() instanceof PrimitiveBox){
                        m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess().getFirstChild(), m.getCall().getArguments().getFirstArgument().getFirstChild().getFirstChild(),  op.get()));
                    } else {
                        m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(),  op.get()));
                    }
                }

            }



        } else if (node instanceof ErasurePointNode){
            ErasurePointNode boxingPoint = (ErasurePointNode)node;
            ExpressionNode inner = boxingPoint.getValue();

            TypeVariable originalType = inner.getTypeVariable();
            TypeVariable targetType = boxingPoint.getTypeVariable();

            if (targetType.isSingleType()){
                targetType = targetType.getTypeDefinition();
            }

            //            if (originalType == null){
            //                return;
            //            } 
            //
            //            if (boxingPoint.canElide() && originalType.equals(targetType)){
            //                return;
            //            } 


            if (boxingPoint.getErasureOperation() == ErasureOperation.CONVERTION){
                // CONVERTION

                if (inner instanceof PrimitiveBox  && primitiveType.equals(targetType)){
                    // conversion of erased value. not necessary
                    boxingPoint.getParent().replace(boxingPoint, inner.getFirstChild() );

                } else if (inner instanceof PrimitiveBooleanValue && primitiveType.equals(targetType)){
                    // already erased
                    boxingPoint.getParent().replace(boxingPoint, inner);

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
                } // else, some other type not interest in

            } else if (boxingPoint.getBoxingDirection() == BoxingDirection.BOXING_IN && (targetType.equals(type) || targetType.equals(any))){
                // BOXING IN
                // also box in if expected type is any
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

    private Optional<BooleanOperation> recognizeBooleanOperation(String name) {
        if ("or".equals(name)){
            return Optional.of(BooleanOperation.BitOr);
        } else if ("and".equals(name)){
            return Optional.of(BooleanOperation.BitAnd);
        } else if ("xor".equals(name)){
            return Optional.of(BooleanOperation.BitXor);
        } 

        return Optional.empty();
    }


}
