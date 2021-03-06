package lense.compiler.crosscompile;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.CastNode;
import lense.compiler.ast.ConditionalStatement;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.IndexedPropertyReadNode;
import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeAssistant;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * Classifies cutting points as boxing or unboxing regardless of type
 * 
 */
public final class ErasurePointClassificationVisitor implements Visitor<AstNode> {

	private TypeVariable expectedType;
	private LenseTypeAssistant typeAssistant;


	
	public ErasurePointClassificationVisitor(SemanticContext semanticContext) {
		this.typeAssistant = new LenseTypeAssistant(semanticContext);
	}
	

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		if (node instanceof MethodDeclarationNode){
			this.expectedType = ((MethodDeclarationNode)node).getReturnType().getTypeVariable();
			if (this.expectedType == null){
				this.expectedType = ((MethodDeclarationNode)node).getReturnType().getTypeParameter();
			}

		} else if (node instanceof ConstructorDeclarationNode){
			this.expectedType = ((ConstructorDeclarationNode)node).getReturnType().getTypeVariable();
		} else if (node instanceof ReturnNode){
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
			
			if (r.getExpectedType() == null){
			    r.setExpectedType(r.getTypeVariable());
			}

			r.replace(val,  ErasurePointNode.convertTo(val, r.getExpectedType()));
		} else if ( node instanceof ConditionalStatement conditional) {
			
			var expression = conditional.getCondition();
			
			expression.getParent().replace(expression, ErasurePointNode.convertToPrimitive(expression));
			
		} else if (node instanceof AssignmentNode a){

			a.replace(a.getRight(), ErasurePointNode.convertTo(a.getRight(), ((ExpressionNode)a.getLeft()).getTypeVariable()));

			if (a.getLeft() instanceof ErasurePointNode) {
				 ExpressionNode plain = ((ErasurePointNode)a.getLeft()).elideAll();
				 a.replace((AstNode)a.getLeft(), plain);
			}
			
		}else if (node instanceof VariableDeclarationNode v){

			if (v.getInitializer() !=null){
			    
			    TypeVariable type =  v.getInfo() == null ?  v.getTypeVariable() : v.getInfo().getTypeVariable();
			    
				v.replace(v.getInitializer(), ErasurePointNode.convertTo(v.getInitializer(), type));
			}
		} else if (node instanceof IndexedPropertyReadNode m) {
	
			if (!(typeAssistant.isAssignableTo(m.getTypeVariable(), LenseTypeSystem.Void())).matches() ){
				// outbox return 
				m.getParent().replace(m, ErasurePointNode.unbox(m, m.getTypeVariable()));
			}	
		}else if (node instanceof MethodInvocationNode m ){

			if (!m.isTupleAccessMethod() && !(typeAssistant.isAssignableTo(m.getTypeVariable(), LenseTypeSystem.Void())).matches() ){
				// outbox return 

			
				if (m.isIndexDerivedMethod() && m.getCall().getName().equals("get")){
	                m.getParent().replace(m, ErasurePointNode.unbox(m, m.getTypeVariable()));
	            } else {
	                m.getParent().replace(m, ErasurePointNode.convertTo(m, m.getTypeVariable()));
	            }
			}
			
			
		} else if (node instanceof FieldOrPropertyAccessNode){

			FieldOrPropertyAccessNode m = (FieldOrPropertyAccessNode)node;
			m.getParent().replace(m, ErasurePointNode.convertTo(m, m.getTypeVariable()));
			
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
				if (theItem instanceof LiteralExpressionNode) {
					item.setExpectedType(((LiteralExpressionNode) theItem).getTypeVariable());
				} else {
					return;
				}
				
			}

			if (theItem instanceof ErasurePointNode box){
			
				if (box.isBoxingDirectionOut()) {
					// trying to box in a box out = no boxing
					item.replace(box, box.getValue());
				} // else is already a box in
				return;
			}

			TypeVariable typevar =  item.getExpectedType();
			
			ErasurePointNode box = ErasurePointNode.box((ExpressionNode)theItem, item.getExpectedType());
			
			box.setCanElide(!typevar.isCalculated());
			
			item.replace(theItem, box);
		} 
	}



}
