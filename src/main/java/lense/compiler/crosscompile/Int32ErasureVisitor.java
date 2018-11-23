package lense.compiler.crosscompile;

import java.util.HashMap;
import java.util.Map;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.VariableInfo;
import lense.compiler.crosscompile.java.JavaTypeKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class Int32ErasureVisitor implements Visitor<AstNode> {


    private TypeDefinition type;
    private PrimitiveTypeDefinition primitiveType;

    private Map<String, ArithmeticOperation> ops = new HashMap<>();

    public Int32ErasureVisitor (){
        type = LenseTypeSystem.Int32();
        primitiveType = PrimitiveTypeDefinition.INT;

        for (ArithmeticOperation op : ArithmeticOperation.values()){
            ops.put(op.equivalentMethod(),op);
        }
    }

    @Override
    public void startVisit() {}

    @Override
    public void endVisit() {}

    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {

        if (node instanceof ReturnNode){
            ReturnNode r = (ReturnNode)node;

            TypeVariable tv = r.getExpectedType();

            if (tv != null && tv.isFixed() &&  LenseTypeSystem.getInstance().isAssignableTo(tv, type)) {


                r.setExpectedType(primitiveType);

            }
        } else if (node instanceof VariableDeclarationNode || node instanceof InstanceOfNode){
            return VisitorNext.Children;
        } else if (node instanceof NumericValue){
            NumericValue n = (NumericValue) node;

            if (LenseTypeSystem.getInstance().isAssignableTo(n.getTypeVariable(), type)){
                n.setTypeVariable(primitiveType);
            }

        } else if (node instanceof TypedNode
                && !(node instanceof BoxingPointNode )
                && !(node.getParent() instanceof VariableDeclarationNode )
                && !(node.getParent() instanceof InstanceOfNode )
                ) {
            TypedNode t = (TypedNode) node;

            TypeVariable tv = t.getTypeVariable();

            if (tv != null && tv.isFixed() && !isTupleAccess(node) && LenseTypeSystem.getInstance().isAssignableTo(tv, type)) {

                if (node instanceof CastNode){
                    CastNode c = (CastNode)node;

                    node.getParent().replace(node, c.getFirstChild());

                } else {
                    t.setTypeVariable(primitiveType);
                }

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
    public void visitAfterChildren(AstNode node) {

        if (node instanceof InstanceOfNode) {
            InstanceOfNode ion = (InstanceOfNode)node;

            final TypeDefinition typeDefinition = ((TypedNode)ion.getFirstChild()).getTypeVariable().getTypeDefinition();
            final TypeDefinition targetType = ion.getTypeNode().getTypeVariable().getTypeDefinition();
            if (targetType.equals(type) ){

                // the content is already of corresponding primitive
                if (typeDefinition.equals(primitiveType)){
                    node.getParent().replace(ion, new PrimitiveBooleanValue(true));
                } 
            } else {
                // test is another type
                if (typeDefinition.equals(primitiveType)){
                    node.getParent().replace(ion, new PrimitiveBooleanValue(LenseTypeSystem.isAssignableTo(type, targetType)));
                }

            }
        } else if (node instanceof VariableDeclarationNode){
            VariableDeclarationNode dec = (VariableDeclarationNode)node;

            if (dec.getInitializer() != null &&  primitiveType.equals(dec.getInitializer().getTypeVariable())){
                dec.setTypeNode(new TypeNode(primitiveType));
                final VariableInfo varInfo = dec.getInfo();

                varInfo.setTypeVariable(primitiveType);

                VariableRange limit = VariableRange.extractFrom(dec.getInitializer());

                limit.getMax().ifPresent(m -> varInfo.setMaximumValue(m));
                limit.getMin().ifPresent(m -> varInfo.setMininumValue(m));

                varInfo.setIncludeMaximum(limit.isIncludeMax());

            }

        } else if (node instanceof NewInstanceCreationNode) {
            NewInstanceCreationNode constructor = (NewInstanceCreationNode)node;

            if (constructor.getConstructor() != null && constructor.getTypeNode().getTypeVariable().getTypeDefinition().equals(primitiveType)){

                if (constructor.getConstructor().isImplicit() && ((ExpressionNode)constructor.getArguments().getFirstArgument().getFirstChild()).getTypeVariable().equals(primitiveType)){
                    // remove the conversion constrcutor
                    constructor.getParent().replace(constructor, constructor.getArguments().getFirstArgument().getFirstChild());
                }
                // revert typing 
                constructor.setTypeVariable(type);
            }
        } else if (node instanceof ForEachNode) {
            ForEachNode f = (ForEachNode)node;
            VariableInfo varInfo = f.getVariableDeclarationNode().getInfo();
            
            if (varInfo.getTypeVariable().equals(primitiveType)){
                
                if (!varInfo.getMaximum().isPresent() || !varInfo.getMinimum().isPresent()){
                    // revert to  type
                    varInfo.setTypeVariable(type);
                } else if(f.getContainer() instanceof MethodInvocationNode /*&& f.getContainer().getProperty("isRange", Boolean.class).orElse(false)*/){
                    MethodInvocationNode m = (MethodInvocationNode) f.getContainer();
                    
                    AstNode unboxAccess = promoteNodeType(primitiveType, m.getAccess());
                    AstNode unboxArgument = promoteNodeType(primitiveType,  m.getCall().getArguments().getFirstArgument().getFirstChild());
                    
                    MethodInvocationNode inv = new MethodInvocationNode(unboxAccess , m.getCall().getName() ,new ArgumentListItemNode(1, unboxArgument));
                    inv.setTypeMember(m.getTypeMember());
                    inv.setTypeVariable(m.getTypeVariable());
                    inv.setScanPosition(inv.getScanPosition());

                    m.getParent().replace(m, inv);
                    
                }
            }
            
        } else if (node instanceof MethodInvocationNode) {
            MethodInvocationNode m = (MethodInvocationNode) node;

            TypedNode access = (TypedNode)m.getAccess();
            if (access != null  && access.getTypeVariable() != null) {


                if (access.getTypeVariable().getTypeDefinition().equals(primitiveType)){
                    ArithmeticOperation op = this.ops.get(m.getCall().getName());

                    if (op != null){
                        // Arithmetic Operations only have 0 or 1 arguments

                        //                        if (isPrimitiveOperation(op) && ((TypedNode)m.getCall().getFirstChild().getFirstChild().getFirstChild()).getTypeVariable().getTypeDefinition().equals(primitiveType) ){
                        //                            m.getParent().replace(m, new PrimitiveArithmeticOperationsNode(primitiveType,m.getAccess(), m.getCall().getFirstChild().getFirstChild(), op));
                        //                        } else {

                        // determine if the other part is a compatible number
                        AstNode right = m.getCall().getArguments().getFirstArgument().getFirstChild();

                        VariableRange maxRange = VariableRange.forType(primitiveType);
                        
                        VariableRange leftRange = VariableRange.extractFrom((AstNode) access);

                        VariableRange rightRange = VariableRange.extractFrom(right);

                        VariableRange range = leftRange.operate(rightRange, op);

                        TypeDefinition opType = m.getTypeVariable().getTypeDefinition();

                        if (maxRange.contains(range)){
                            //result argument is in range of the primitive operation

                            while (!isNodePromotableToPrimitive(right)){
                                if (right instanceof BoxingPointNode && ((BoxingPointNode) right).canElide()){
                                    right = right.getFirstChild();
                                } else if (right instanceof NewInstanceCreationNode
                                        && ((NewInstanceCreationNode) right).getConstructor() != null
                                        && ((NewInstanceCreationNode) right).getConstructor().isImplicit()  
                                        &&((NewInstanceCreationNode) right).getArguments().getChildren().size() == 1){
                                    right = ((NewInstanceCreationNode)right).getArguments().getFirstChild().getFirstChild();
                                } else {
                                    break;
                                }
                            }

                            if (right instanceof VariableReadNode){
                                VariableReadNode v = (VariableReadNode)right;

                                final VariableInfo varInfo = v.getVariableInfo();
                                rightRange.getMin().ifPresent( it -> varInfo.setMininumValue(it));
                                rightRange.getMax().ifPresent( it -> varInfo.setMaximumValue(it));
                                
                                varInfo.setTypeVariable(primitiveType);
                                v.setTypeVariable(primitiveType);

                                
                                op = coerseToPrimitiveOperation(op);
                                opType = primitiveType;


                                m.getParent().replace(m, new PrimitiveArithmeticOperationsNode(opType,m.getAccess(), right, op));
                            } else if (right instanceof NumericValue){
                                m.getCall().getArguments().getFirstArgument().replace(m.getCall().getArguments().getFirstArgument().getFirstChild(), right);
                            }
                        } else if (((TypedNode)right).getTypeVariable().getTypeDefinition().equals(primitiveType)){
                            // arguments are still primitive
                            m.getParent().replace(m, new PrimitiveArithmeticOperationsNode(primitiveType,m.getAccess(), m.getCall().getFirstChild().getFirstChild(), op));
                        }

                        //	                    }
                    } else if (m.getCall().getName().equals("asString")) {
                        m.getParent().replace(m, new MethodInvocationOnPrimitiveNode(primitiveType, m));
                    } else {
                        // box then again
                        PrimitiveBox boxAccess = new PrimitiveBox(primitiveType, m.getAccess());
                        PrimitiveBox boxArgument = new PrimitiveBox(primitiveType, m.getCall().getArguments().getFirstArgument().getFirstChild());

                        // m.replace(m.getAccess(), boxAccess);
                        // m.getCall().getFirstChild().getFirstChild().replace(m.getCall().getFirstChild().getFirstChild().getFirstChild() ,boxArgument );

                        MethodInvocationNode inv = new MethodInvocationNode(boxAccess , m.getCall().getName() ,new ArgumentListItemNode(1, boxArgument));
                        inv.setTypeMember(m.getTypeMember());
                        inv.setTypeVariable(m.getTypeVariable());
                        inv.setScanPosition(inv.getScanPosition());

                        m.getParent().replace(m, inv);
                    }
                } else if (access instanceof NumericValue && m.getCall().getArguments().getChildren().size() == 1){
                    NumericValue n = (NumericValue)access;

                    AstNode right = m.getCall().getArguments().getFirstArgument().getFirstChild();


                    if (right instanceof TypedNode &&  ((TypedNode) right).getTypeVariable().getTypeDefinition().equals(primitiveType) && isInRange(n.getValue(), primitiveType)){
                        for (Operation op : Operation.values()){
                            if (op.getEquivalentMethodName().map( a -> a.equalsIgnoreCase(m.getCall().getName())).orElse(false)){


                                PrimitiveComparisonNode c = new PrimitiveComparisonNode(op);
                                c.add(new PrimitiveNumericValue(primitiveType, n));
                                c.add( m.getCall().getFirstChild().getFirstChild().getFirstChild());


                                node.getParent().replace(node, c);


                                break;
                            }
                        }

                    }

                }

            }
        } else if (node instanceof BoxingPointNode){
            BoxingPointNode a = (BoxingPointNode)node;
            ExpressionNode val = a.getValue();

            if (val.getTypeVariable() == null){
                return;
            } 


            if (a.canElide() && val.getTypeVariable().equals(a.getTypeVariable())){
                if (val instanceof NumericValue && ((NumericValue)val).getTypeVariable().equals(this.type)) {
                    ((NumericValue)val).setTypeVariable(primitiveType);

                } else {
                    a.getParent().replace(a, val);
                }

                return;
            } 

            if (a.isBoxingDirectionOut()){
                // OUT BOXING

                if ( val instanceof NumericValue && val.getTypeVariable().getTypeDefinition().equals(type)) {
                    if(a.getReferenceNode() instanceof ArgumentListItemNode){
                        ArgumentListItemNode ref = (ArgumentListItemNode)a.getReferenceNode();
                        if (ref.isGeneric()){
                            a.getParent().replace(a, new PrimitiveBox(primitiveType,val));
                            return;
                        }
                    } 


                    a.getParent().replace(a, new PrimitiveNumericValue(primitiveType, ((NumericValue)val)));


                } else if (a.getTypeVariable() != null && !val.getTypeVariable().isFixed() && a.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive){

                    a.getParent().replace(a, new PrimitiveUnbox( primitiveType,val));

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
                    // val is already a primitive boolean
                    if ((val instanceof VariableReadNode && a.canElide()) || val instanceof PrimitiveArithmeticOperationsNode) {
                        a.getParent().replace(a, val);
                    } else {
                        a.getParent().replace(a, new PrimitiveBox(primitiveType, val));
                    }

                } else if (val instanceof MethodInvocationNode) {
                    MethodInvocationNode m = (MethodInvocationNode)val;
                    if ( m.getTypeVariable().isSingleType() && m.getTypeVariable().getTypeDefinition().getName().equals(type.getName())) {
                        a.getParent().replace(a, new PrimitiveBox(primitiveType, val));
                    }
                } else if (val instanceof CastNode) {
                    // no-op

                } else {

                }

            }


        }

    }

    private AstNode promoteNodeType(PrimitiveTypeDefinition primitiveType, AstNode node) {
      
        if (node instanceof NumericValue){
            return new PrimitiveNumericValue(primitiveType, ((NumericValue) node));
        } else if (node instanceof NewInstanceCreationNode){
            NewInstanceCreationNode constructor  = (NewInstanceCreationNode)node;
            if (constructor.getConstructor() != null && constructor.getConstructor().isImplicit() ){

                if (constructor.getTypeNode().getTypeVariable().getTypeDefinition().equals(primitiveType) && ((ExpressionNode)constructor.getArguments().getFirstArgument().getFirstChild()).getTypeVariable().equals(primitiveType)){
                    // remove the conversion constrcutor
                   return constructor.getArguments().getFirstArgument().getFirstChild();
                } 
            }
        }  else if (node instanceof PrimitiveBox ){
            return promoteNodeType(primitiveType, node.getFirstChild());
        }
        
        return node;
           
    }

    private boolean isNodePromotableToPrimitive(AstNode node) {
        return node instanceof VariableReadNode || node instanceof NumericValue;
    }

    private ArithmeticOperation coerseToPrimitiveOperation(ArithmeticOperation op) {
        switch (op){
        case Addition:
            return ArithmeticOperation.WrapAddition;
        case Subtraction:
            return ArithmeticOperation.WrapSubtraction; 
        case Multiplication:
            return ArithmeticOperation.WrapMultiplication; 
        default:
            return op;
        }
    }

    private boolean isPrimitiveOperation(ArithmeticOperation op) {
        switch (op){
        case Addition:
        case Power:
        case Subtraction:
        case Multiplication:
        case Division:
        case IntegerDivision:
            return false;
        default:
            return true;
        }
    }

    private boolean isInRange(Number number, TypeDefinition type) {
        if (type == PrimitiveTypeDefinition.INT){
            return number.longValue() >= Integer.MIN_VALUE && number.longValue() <= Integer.MAX_VALUE;
        } else if (type == PrimitiveTypeDefinition.LONG){
            return number.longValue() >= Long.MIN_VALUE && number.longValue() <= Long.MAX_VALUE;
        }
        return false;
    }

}
