package lense.compiler.crosscompile;

import java.util.function.Function;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.crosscompile.BoxingPointNode.BoxingDirection;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
public class BoxingPointClassificationVisitor implements Visitor<AstNode> {
			 
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
	
		}else if (node instanceof MethodInvocationNode){
			MethodInvocationNode m = (MethodInvocationNode)node;
			
			if (!m.getTypeVariable().getTypeDefinition().getName().equals(LenseTypeSystem.Void().getName())){
				
				m.getParent().replace(m, new BoxingPointNode(m, m, BoxingDirection.BOXING_OUT));
			}
			
		} else if (node instanceof FieldOrPropertyAccessNode){
			
			FieldOrPropertyAccessNode m = (FieldOrPropertyAccessNode)node;
			m.getParent().replace(m, new BoxingPointNode(m, m, BoxingDirection.BOXING_OUT));
		} else if (node instanceof ArgumentListNode){
			
			if (node.getParent().getParent() instanceof MethodInvocationNode){
				if (((MethodInvocationNode)node.getParent().getParent()).isPropertyDerivedMethod()){
					return;
				}
			}
			for (AstNode a  : node.getChildren()){
				 ArgumentListItemNode item  = (ArgumentListItemNode)a;
				 if (item.getExpectedType() == null){
					 continue;
				 }

				 Function<AstNode, TypeVariable> f = i -> ((ArgumentListItemNode)i).getExpectedType();
				 if (!(item.getFirstChild() instanceof BoxingPointNode)){
					 item.replace(item.getFirstChild(), new BoxingPointNode((ExpressionNode)item.getFirstChild(), item, f , BoxingDirection.BOXING_IN));
				 } else {
					 continue;
				 }
				
			}
		} 
	}



}
