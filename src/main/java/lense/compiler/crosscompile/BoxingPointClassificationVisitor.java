package lense.compiler.crosscompile;

import java.util.function.Function;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.IndexedAccessNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.crosscompile.BoxingPointNode.BoxingDirection;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * Classifies cuting points as boxing or unboxing regardless of type
 * 
 */
public final class BoxingPointClassificationVisitor implements Visitor<AstNode> {

	private TypeVariable expectedType;

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		if (node instanceof MethodDeclarationNode){
			this.expectedType = ((MethodDeclarationNode)node).getReturnType().getTypeVariable();
			if (this.expectedType == null){
				this.expectedType = ((MethodDeclarationNode)node).getReturnType().getTypeParameter();
			}

		} else if (node instanceof ConstructorDeclarationNode){
			this.expectedType = ((ConstructorDeclarationNode)node).getReturnType().getTypeVariable();
		}  else if (node instanceof ReturnNode){
			((ReturnNode)node).setExpectedType(this.expectedType );
			this.expectedType  = null;
		}
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof ReturnNode){
			ReturnNode r = (ReturnNode)node;
			ExpressionNode val = r.getValue();

			r.replace(val, new BoxingPointNode(val, r, u -> ((ReturnNode)u).getExpectedType(),BoxingDirection.BOXING_OUT) );
		} else if (node instanceof AssignmentNode){
			AssignmentNode a = (AssignmentNode)node;

			a.replace(a.getRight(), new BoxingPointNode(a.getRight(), (AstNode)a.getLeft(), BoxingDirection.BOXING_OUT));

		}else if (node instanceof VariableDeclarationNode){
			VariableDeclarationNode v = (VariableDeclarationNode)node;

			if (v.getInitializer() !=null){
				v.replace(v.getInitializer(), new BoxingPointNode(v.getInitializer(), v, BoxingDirection.BOXING_OUT));
			}
		}else if (node instanceof IndexedAccessNode) {
			IndexedAccessNode m = (IndexedAccessNode)node;

			if (!(LenseTypeSystem.getInstance().isAssignableTo(m.getTypeVariable(), LenseTypeSystem.Void()))){
				// outbox return 
				m.getParent().replace(m, new BoxingPointNode(m, m, BoxingDirection.BOXING_OUT));
			}	
		}else if (node instanceof MethodInvocationNode){
			MethodInvocationNode m = (MethodInvocationNode)node;

			if (!m.isTupleAccessMethod() && !(LenseTypeSystem.getInstance().isAssignableTo(m.getTypeVariable(), LenseTypeSystem.Void()))){
				// outbox return 

				m.getParent().replace(m, new BoxingPointNode(m, m, BoxingDirection.BOXING_OUT));

			}
		} else if (node instanceof FieldOrPropertyAccessNode){

			FieldOrPropertyAccessNode m = (FieldOrPropertyAccessNode)node;
			m.getParent().replace(m, new BoxingPointNode(m, m, BoxingDirection.BOXING_OUT));
		} else if (node instanceof ArgumentListItemNode){
			ArgumentListItemNode item  = (ArgumentListItemNode)node;

			AstNode theItem = item.getFirstChild();

			if (theItem instanceof CaptureReifiedTypesNode) {
				return;
			} else if (theItem instanceof CastNode) {
				 if(((CastNode) theItem).isTupleAccessMethod() || theItem.getFirstChild() instanceof MethodInvocationNode && ((MethodInvocationNode)theItem.getFirstChild()).isTupleAccessMethod()) {
					 return;
				 }
			}else if (theItem instanceof ArgumentListNode) {

				for (AstNode a : theItem.getChildren()) {
					visitAfterChildren(a);
				}
				return;
			}


			if (item.getExpectedType() == null){
				return;
			}

			if (theItem instanceof BoxingPointNode){
				BoxingPointNode box = (BoxingPointNode)theItem;

				if (box.isBoxingDirectionOut()) {
					// trying to box in a box out = no boxing
					item.replace(box, box.getReferenceNode());
				} // else is already a box in
				return;
			}

			TypeVariable typevar =  item.getExpectedType();
			
			Function<AstNode, TypeVariable> f = i -> ((ArgumentListItemNode)i).getExpectedType();
			
			BoxingPointNode box = new BoxingPointNode((ExpressionNode)theItem, item, f , BoxingDirection.BOXING_IN);
			
			box.setCanElide(!typevar.isCalculated());
			
			item.replace(theItem, box);



		} 
	}



}
