package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.crosscompile.java.JavaTypeKind;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public class BooleanErasureVisitor implements Visitor<AstNode> {


	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {

		if (node instanceof ReturnNode){
			ReturnNode r = (ReturnNode)node;

			TypeVariable tv = r.getExpectedType();

			if (tv != null && tv.isFixed() &&  LenseTypeSystem.getInstance().isAssignableTo(tv, LenseTypeSystem.Boolean())) {
				r.setExpectedType(PrimitiveTypeDefinition.BOOLEAN);
			} else if (tv != null && tv.isFixed() &&  LenseTypeSystem.getInstance().isAssignableTo(tv, LenseTypeSystem.Int32())) {
				r.setExpectedType(PrimitiveTypeDefinition.INT);
			}
			
		} else if (node instanceof TypedNode && !(node instanceof BoxingPointNode )) {
			TypedNode t = (TypedNode) node;

			TypeVariable tv = t.getTypeVariable();

			if (tv != null && tv.isFixed() && !isTupleAccess(node) && LenseTypeSystem.getInstance().isAssignableTo(tv, LenseTypeSystem.Boolean())) {
				t.setTypeVariable(PrimitiveTypeDefinition.BOOLEAN);
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

		if (node instanceof MethodInvocationNode) {
			MethodInvocationNode m = (MethodInvocationNode) node;

			TypedNode access = (TypedNode)m.getAccess();
			if (access != null  && access.getTypeVariable() != null && access.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive) {

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
			}
		} else if (node instanceof BoxingPointNode){
			BoxingPointNode a = (BoxingPointNode)node;
			ExpressionNode val = a.getValue();

			if (val.getTypeVariable() == null){
				return;
			} 

			
			if (a.canElide() && val.getTypeVariable().equals(a.getTypeVariable())){
				if (val instanceof BooleanValue) {
					a.getParent().replace(a, new PrimitiveBooleanValue(((BooleanValue)val).isValue()));
				} else {
					a.getParent().replace(a, val);
				}
				
				return;
			} 
			
			if (a.isBoxingDirectionOut()){
				// OUT BOXING

			    if ( val instanceof BooleanValue) {
					if(a.getReferenceNode() instanceof ArgumentListItemNode){
						ArgumentListItemNode ref = (ArgumentListItemNode)a.getReferenceNode();
						if (ref.isGeneric()){
							a.getParent().replace(a, new PrimitiveBox(PrimitiveTypeDefinition.BOOLEAN, val));
							return;
						}
					} 

					a.getParent().replace(a, new PrimitiveBooleanValue(((BooleanValue)val).isValue()));
				} else if (a.getTypeVariable() != null && !val.getTypeVariable().isFixed() && a.getTypeVariable().getTypeDefinition().getKind() == JavaTypeKind.Primitive){
					
					// TODO check which primitive , it may be other than boolean
					a.getParent().replace(a, new PrimitiveUnbox( PrimitiveTypeDefinition.BOOLEAN, val));
					
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
					if ((val instanceof VariableReadNode && a.canElide()) || val instanceof PrimitiveBooleanOperationsNode) {
						a.getParent().replace(a, val);
					} else {
						a.getParent().replace(a, new PrimitiveBox(PrimitiveTypeDefinition.BOOLEAN, val));
					}
			
				} else if (val instanceof MethodInvocationNode) {
					MethodInvocationNode m = (MethodInvocationNode)val;
					if ( m.getTypeVariable().isSingleType() && m.getTypeVariable().getTypeDefinition().getName().equals(LenseTypeSystem.Boolean().getName())) {
						a.getParent().replace(a, new PrimitiveBox(PrimitiveTypeDefinition.BOOLEAN, val));
					}
				} else if (val instanceof CastNode) {
					// no-op
					
				} else {
					System.out.println(val.getClass().getName());
				}

			}
		

		}

	}

}
