package lense.compiler.crosscompile;

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
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.crosscompile.ErasurePointNode.BoxingDirection;
import lense.compiler.crosscompile.ErasurePointNode.ErasureOperation;
import lense.compiler.phases.AbstractScopedVisitor;
import lense.compiler.type.IndexerProperty;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.LenseTypeDefinition;
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
	private LenseTypeAssistant typeAssistant;

    public BooleanErasureVisitor(SemanticContext semanticContext) {
        super(semanticContext);
        this.typeAssistant = new LenseTypeAssistant(semanticContext);
    }

    @Override
    protected Optional<LenseTypeDefinition> getCurrentType() {
        return Optional.ofNullable(currentType);
    }

    private boolean isBooleanNode(ExpressionNode node) {
       return node instanceof BooleanOperatorNode
    		   || node instanceof ComparisonNode &&  (((ComparisonNode)node).getOperation() == Operation.ReferenceEquals ||  ((ComparisonNode)node).getOperation() == Operation.ReferenceDifferent)
    		   || node instanceof PreBooleanUnaryExpression;
       
              // || node instanceof InstanceOfNode;
               
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
    public VisitorNext doVisitBeforeChildren(AstNode node) {
        if (node instanceof ClassTypeNode) {
            this.currentType = ((ClassTypeNode) node).getTypeDefinition();
        } else if (node instanceof InstanceOfNode){
            ((InstanceOfNode) node).setTypeVariable(erasedType);
        } else if (node instanceof ReturnNode){
            ReturnNode r = (ReturnNode)node;

            TypeVariable tv = r.getExpectedType();

            if (tv != null && tv.isFixed() &&  typeAssistant.isAssignableTo(tv, type).matches() ) {
                r.setExpectedType(erasedType);
                if (r.getFirstChild() instanceof ErasurePointNode){
                    ErasurePointNode p =(ErasurePointNode)r.getFirstChild();
                    p.setTypeVariable(erasedType);
                }
            }

        } else if (node instanceof BooleanOperatorNode){
            BooleanOperatorNode op = ((BooleanOperatorNode)node);

            if (node.getChildren().size() == 1) {
            	  final ExpressionNode expr = (ExpressionNode) node.getChildren().get(0);
                  if (expr instanceof ErasurePointNode){
                      ErasurePointNode p = (ErasurePointNode)expr;
                      if (p.getTypeVariable().equals(type) && p.canElide()){
                          op.replace(p, p.getFirstChild());
                      } else {
                    	  node.getParent().replace(node, node.getChildren().get(0));
                      }
                  } else {
                	  node.getParent().replace(node, node.getChildren().get(0));
                  }
            	
            } else {
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
            }
            

        } else if (node instanceof ComparisonNode){
        	ComparisonNode op = ((ComparisonNode)node);
  
        	if (op.getOperation() == Operation.ReferenceEquals) {
        		op.setTypeVariable(erasedType);
        	}
        }  else if (node instanceof MethodInvocationNode){

            MethodInvocationNode m = (MethodInvocationNode)node;
            TypeMember indexer = m.getTypeMember();

            if (m.isIndexDerivedMethod() && indexer instanceof IndexerProperty){

                if ("set".equals(m.getCall().getName())) {
                    TypeVariable parameterType = ((IndexerProperty)indexer).getReturningType();

                    ArgumentListItemNode n  = m.getCall().getArguments().getLastArgument();

                    AstNode arg = n.getFirstChild();
                    TypeVariable argType = ((TypedNode)arg).getTypeVariable();

                    if (argType.equals(type)){
                        if (arg instanceof ErasurePointNode ){
                            if (parameterType.isFixed() && parameterType.equals(type)){
                                //arg.getParent().replace(arg, arg.getFirstChild());
                                arg.getParent().replace(arg, ErasurePointNode.convertTo((ExpressionNode) arg.getFirstChild(), erasedType));
                                
                            } else {
                                ErasurePointNode p = ErasurePointNode.box((ExpressionNode) arg.getFirstChild(), type);
                                p.setCanElide(false);
                                
                                arg.getParent().replace(arg, p);
                            }

                        } else {
                            
                            AstNode parent = arg.getParent();
                                    
                            ErasurePointNode p = ErasurePointNode.box((ExpressionNode) arg, type);
                            p.setCanElide(false);
                            
                            parent.replace(arg, p);
                        }
                        
                    } 
                } else {
                    TypeVariable parameterType = ((IndexerProperty)indexer).getReturningType();

                    AstNode parent = m.getParent();

                    TypeVariable base = ((TypedNode)m.getAccess()).getTypeVariable();
                    
                    if (base.equals(currentType) && parent instanceof ErasurePointNode ){
                        
                        TypeVariable argType = ((ErasurePointNode)parent).getTypeVariable();
                        
                        if ( argType.getUpperBound().equals(type) && !parameterType.isCalculated() && parameterType.getUpperBound().equals(type)){
                            
                            AstNode grandParent = parent.getParent();
                            
                            if (grandParent instanceof ErasurePointNode && ((ErasurePointNode)grandParent).getTypeVariable().equals(primitiveType) ){
                                grandParent.getParent().replace(grandParent, m);
                            } else {
                                parent.getParent().replace(parent, m);
                            }
                        
                        } 
                    } 
                }
            } else {
            	// assume all fixed Boolean returns will be erased to boolean primitive
                
            	if (m.getTypeVariable().isFixed() && m.getTypeVariable().equals(type)){         
                     m.setTypeVariable(erasedType);
                }
            	
            	// assume all fixed Boolean parameters will be erased to boolean primitive
                
            	for (ArgumentListItemNode item : m.getCall().getArguments().getChildren(ArgumentListItemNode.class)) {
            		AstNode arg = item.getFirstChild();
            		
            		if (arg instanceof TypedNode) {
            			TypeVariable argType = ((TypedNode)arg).getTypeVariable();
                		
                		if (type.equals(argType)) {
                			if (type.isFixed()) {
                				item.setExpectedType(erasedType);
                    			
                    			if (arg instanceof ErasurePointNode && ((ErasurePointNode) arg).getBoxingDirection() == BoxingDirection.BOXING_IN ){
                                     arg.getParent().replace(arg, ErasurePointNode.convertTo((ExpressionNode) arg.getFirstChild(), erasedType));
                  
                        			
                                } 
                			} 
                			
                		}
            		}
            		
            	}
            }

        }  else if (node instanceof PreBooleanUnaryExpression){

            final ExpressionNode condition =  (ExpressionNode)((PreBooleanUnaryExpression) node).getFirstChild();
            if (condition instanceof ErasurePointNode /*&& node.getParent() instanceof ConditionalStatement*/){
                ErasurePointNode p = (ErasurePointNode)condition;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    node.replace(p, p.getFirstChild());
                }
            }
        }  else if (node instanceof ConditionalStatement){

            ExpressionNode condition =  ((ConditionalStatement) node).getCondition();
            while (condition instanceof ErasurePointNode){
                ErasurePointNode p = (ErasurePointNode)condition;
                if (p.getTypeVariable().equals(type) && p.canElide()){
                    node.replace(p, p.getFirstChild());
                }
                var newCondition =  ((ConditionalStatement) node).getCondition();
                if (newCondition == condition) {
                	break;
                }
                condition = newCondition;
            }
        } else if (node instanceof VariableDeclarationNode){
            VariableDeclarationNode var = (VariableDeclarationNode)node;
            
            TypeVariable vartype = var.getInfo() == null ? var.getTypeVariable() : var.getInfo().getTypeVariable();
            
            if (vartype.equals(type)){
                var.setTypeVariable(erasedType);
                if (var.getInfo() != null){
                    var.getInfo().setTypeVariable(erasedType);
                }
                
                ExpressionNode init = var.getInitializer();
                
                if (init != null){
//                  
//                  if (init instanceof ErasurePointNode){
//                      ErasurePointNode p = (ErasurePointNode)init;
//                      if (p.getTypeVariable().equals(type) && p.canElide()){
//                          node.replace(p, p.getFirstChild());
//                      }
//                  }
//                  
                  init.setTypeVariable(erasedType);
                }
                
            }
        } else if (node instanceof TypedNode
        		&& !(node instanceof BooleanValue )
                && !(node instanceof ErasurePointNode )
                && !(node.getParent() instanceof VariableDeclarationNode )
                && !(node.getParent() instanceof InstanceOfNode)
                && !(node.getParent() instanceof GenericTypeParameterNode )
                && !(node instanceof GenericTypeParameterNode )
                ) {
            TypedNode t = (TypedNode) node;

            TypeVariable tv = t.getTypeVariable();

            if (tv != null && tv.isFixed() && !isTupleAccess(node) && typeAssistant.isAssignableTo(tv, type).matches() ) {
                t.setTypeVariable(erasedType);
            } 

        } 

        return VisitorNext.Children;
    }


    private AstNode primitiveBooleanOperation(AstNode original, BooleanOperation operation) {
    	
    	if (operation == BooleanOperation.LogicNegate && original instanceof PrimitiveBox ) {
    		AstNode inner = original.getFirstChild();
    		
    		return new PrimitiveBox (((PrimitiveBox)original).getTypeVariable().getTypeDefinition(), primitiveBooleanOperation(inner, operation));
    		
    	} else if(operation == BooleanOperation.LogicNegate && original instanceof BooleanValue) {
    		return ((BooleanValue)original).negate();
    	} else if(operation == BooleanOperation.LogicNegate 
    			&& original instanceof PrimitiveBooleanOperationsNode 
    			&& ((PrimitiveBooleanOperationsNode)original).getOperation() == BooleanOperation.LogicNegate) {
//    		double negate
    		return ((PrimitiveBooleanOperationsNode)original).getFirstChild();
    	}
    	return new PrimitiveBooleanOperationsNode(original, operation);
    }
    
    private AstNode unbox(AstNode original) {
    	if(original instanceof PrimitiveBox) {
    		return original.getFirstChild();
    	}
    	return new PrimitiveUnbox(primitiveType, original);
    }
    
    
    @Override
    public void doVisitAfterChildren(AstNode node) {

        if (node instanceof AssertNode){
            AssertNode a = (AssertNode)node;

            if (!a.getCondition().getTypeVariable().equals(primitiveType)){

            	
                a.replace(a.getCondition(), unbox(a.getCondition()));
            }

        } else
        	if (node instanceof MethodInvocationNode) {
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
                        m.getParent().replace(m, primitiveBooleanOperation(m.getAccess(), BooleanOperation.LogicNegate));
                    }
                }
            } else if (m.getCall().getName().equals("asString")){

                TypeVariable accessType = ((TypedNode)m.getAccess()).getTypeVariable();
                
                if (primitiveType.equals(accessType)){
                    m.getParent().replace(m, new MethodInvocationOnPrimitiveNode(primitiveType, m));
                }
            } else if (m.getCall().getName().equals("negate")){

                if (m.getAccess() instanceof ErasurePointNode){
                    ErasurePointNode accessPoint  = (ErasurePointNode) m.getAccess();
                    if (primitiveType.equals(accessPoint.getTypeVariable())){
                        m.getParent().replace(m, primitiveBooleanOperation(m.getAccess(), BooleanOperation.LogicNegate));
                    }
                } else {
                    m.getParent().replace(m, primitiveBooleanOperation(m.getAccess(), BooleanOperation.LogicNegate));
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

            if (boxingPoint.getErasureOperation() == ErasureOperation.CONVERTION){
                // CONVERTION

                if (inner instanceof PrimitiveBox  && primitiveType.equals(targetType)){
                    // conversion of erased value. not necessary
                    boxingPoint.getParent().replace(boxingPoint, inner.getFirstChild() );

                } else if (inner instanceof PrimitiveBooleanValue && primitiveType.equals(targetType)){
                    // already erased
                    boxingPoint.getParent().replace(boxingPoint, inner);

                } else if ((type.equals(originalType)  || originalType != null && type.equals(originalType.getUpperBound()) ) && primitiveType.equals(targetType)){
                    // convert to primitive by unboxing

                    if (inner instanceof BooleanValue){
                        // is a literal
                        boxingPoint.getParent().replace(boxingPoint, new PrimitiveBooleanValue(((BooleanValue)inner).isValue()));
                    } else if (isBooleanNode(inner) || inner.getTypeVariable().equals(primitiveType)) {
                        boxingPoint.getParent().replace(boxingPoint, inner);
                    } else {
                        boxingPoint.getParent().replace(boxingPoint, unbox(inner));
                    }


                } else if (primitiveType.equals(originalType) && type.equals(targetType)){
                    // convert to object type by boxing
                    boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(type, inner));
                } else if (( type.equals(targetType) || primitiveType.equals(targetType)) && targetType.equals(originalType)){
                    // the types are expected and already are the same, remove erasure point
                    boxingPoint.getParent().replace(boxingPoint, inner);
                } else if (boxingPoint.getFirstChild() instanceof ErasurePointNode){
                    ErasurePointNode p = (ErasurePointNode)boxingPoint.getFirstChild();
                    if (p.getBoxingDirection() == BoxingDirection.BOXING_OUT){

                        if (p.getTypeVariable().equals(type) || p.getTypeVariable().equals(primitiveType)){
                       
                        }

                    } 
                } // else, some other type not interest in

            } else if (boxingPoint.getBoxingDirection() == BoxingDirection.BOXING_IN ){
                // BOXING IN
                // also box in if expected type is any
                if (inner instanceof BooleanValue){
                    // a literal is already boxed
                    boxingPoint.getParent().replace(boxingPoint, inner); 
                } else if (primitiveType.equals(originalType) && (targetType.equals(type) || targetType.equals(any))){
                    // the original type is a primitive

                    if ((inner instanceof VariableReadNode && boxingPoint.canElide()) ) {
                        // if can be elided, does not box , simple remove the box around inner
                        boxingPoint.getParent().replace(boxingPoint, inner);
                    } else  if (inner instanceof PrimitiveBox){
                        // already correct
                        boxingPoint.getParent().replace(boxingPoint,inner);
                    } else {
                        // box the primitive
                        boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(type, inner));
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
                } else if (boxingPoint.getTypeVariable() != null 
                		&& !originalType.isFixed() 
                		&& !targetType.getTypeDefinition().equals(primitiveType)){

//                    boxingPoint.getParent().replace(boxingPoint, unbox(inner));


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
    }}
