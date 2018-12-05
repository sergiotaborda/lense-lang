package lense.compiler.crosscompile;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.phases.AbstractScopedVisitor;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class BooleanErasureVisitor extends AbstractScopedVisitor {




    private static final PrimitiveTypeDefinition primitiveType = PrimitiveTypeDefinition.BOOLEAN;
    private static final TypeDefinition type = LenseTypeSystem.Boolean();
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
				r.setExpectedType(primitiveType);
			} 
			
		} else if (node instanceof TypedNode && !(node instanceof BoxingPointNode )) {
			TypedNode t = (TypedNode) node;

			TypeVariable tv = t.getTypeVariable();

			if (tv != null && tv.isFixed() && !isTupleAccess(node) && LenseTypeSystem.isAssignableTo(tv, type)) {
				t.setTypeVariable(primitiveType);
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

			TypedNode access = (TypedNode)m.getAccess();
			if (access != null  && access.getTypeVariable() != null && access.getTypeVariable().getTypeDefinition().equals(primitiveType)) {

                if (m.getCall().getName().equals("negate")){

					m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), BooleanOperation.LogicNegate));
				} else if (m.getCall().getName().equals("or")){

					m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitOr));
				} else if (m.getCall().getName().equals("and")){

					m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitAnd));
				} else if (m.getCall().getName().equals("xor")){

					m.getParent().replace(m, new PrimitiveBooleanOperationsNode(m.getAccess(), m.getCall().getFirstChild().getFirstChild(), BooleanOperation.BitXor));
				} else if (m.getCall().getName().equals("equalsTo")){

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
			} else if (access != null  && access.getTypeVariable() != null && access.getTypeVariable().getTypeDefinition().equals(type)){
			    CastNode cast = new CastNode((LenseAstNode) access , type);
			    m.replace((AstNode) access, cast);
			} else if (access != null  &&  access.getTypeVariable() != null //&& access.getTypeVariable().equals(currentType)
			        && m.getCall().getName().equals("bitAt")){
		         
    	           if (m.getTypeVariable().getTypeDefinition().equals(primitiveType) && ((Method)m.getTypeMember()).getReturningType().equals(type)){
    	               // was errased 
    	               System.out.println("Errased: "+  m.getParent().getParent());
    	               
    	               if (m.getParent() instanceof BoxingPointNode){
    	                   
    	               }
    	           }
	         
	            
			}
		} else if (node instanceof BoxingPointNode){
			BoxingPointNode boxingPoint = (BoxingPointNode)node;
			ExpressionNode inner = boxingPoint.getValue();

			if (inner.getTypeVariable() == null){
				return;
			} 

			if (boxingPoint.canElide() && inner.getTypeVariable().equals(boxingPoint.getTypeVariable())){
				if (inner instanceof BooleanValue) {
					boxingPoint.getParent().replace(boxingPoint, new PrimitiveBooleanValue(((BooleanValue)inner).isValue()));
				} else {
					boxingPoint.getParent().replace(boxingPoint, inner);
					
				}
				
				return;
			} 
			
			if (boxingPoint.isBoxingDirectionOut()){
				// OUT BOXING
			    if (inner instanceof BoxingPointNode){
                    BoxingPointNode other = (BoxingPointNode)inner;
                    
                    if (!other.isBoxingDirectionOut()){
                        boxingPoint.getParent().replace(boxingPoint, other.getFirstChild());
                    }
                    
                } 
			    if ( inner instanceof BooleanValue) {
					if(boxingPoint.getReferenceNode() instanceof ArgumentListItemNode){
						ArgumentListItemNode ref = (ArgumentListItemNode)boxingPoint.getReferenceNode();
						if (ref.isGeneric()){
							boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(primitiveType, inner));
							return;
						}
					} 

					boxingPoint.getParent().replace(boxingPoint, new PrimitiveBooleanValue(((BooleanValue)inner).isValue()));
				} else if (boxingPoint.getTypeVariable() != null && !inner.getTypeVariable().isFixed() && boxingPoint.getTypeVariable().getTypeDefinition().equals(primitiveType)){
					

					boxingPoint.getParent().replace(boxingPoint, new PrimitiveUnbox(primitiveType, inner));
					
				}
				// TODO StringConcatenationNode, StringValue

			} else {
				// IN BOXING
			    if (inner instanceof BoxingPointNode){
			        BoxingPointNode other = (BoxingPointNode)inner;
			        
			        if (other.isBoxingDirectionOut()){
			            boxingPoint.getParent().replace(boxingPoint, other.getFirstChild());
			        }
			        
                } else if (inner instanceof BooleanValue){
					boxingPoint.getParent().replace(boxingPoint, inner);
				} else if (inner instanceof NumericValue){
					NumericValue n = (NumericValue)inner;
					n.setTypeVariable(boxingPoint.getTypeVariable());
					boxingPoint.getParent().replace(boxingPoint, inner);
				} else if (inner.getTypeVariable().getTypeDefinition().equals(primitiveType)){
					// val is already a primitive boolean
					if ((inner instanceof VariableReadNode && boxingPoint.canElide()) || inner instanceof PrimitiveBooleanOperationsNode) {
						boxingPoint.getParent().replace(boxingPoint, inner);
					} else {
						boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(primitiveType, inner));
					}
				} else if (inner instanceof MethodInvocationNode) {
					MethodInvocationNode m = (MethodInvocationNode)inner;
					
					
					
					if ( m.getTypeVariable().isSingleType() && m.getTypeVariable().getTypeDefinition().getName().equals(type.getName())) {
						boxingPoint.getParent().replace(boxingPoint, new PrimitiveBox(primitiveType, inner));
					}
				} else if (inner instanceof CastNode) {
					// no-op
					
				} else {
					System.out.println(inner.getClass().getName());
				}

			}
		

		}

	}


}
