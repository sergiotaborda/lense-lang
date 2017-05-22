package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.AssignmentNode.Operation;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.PropertyOperation;
import lense.compiler.context.SemanticContext;

public class ExplicitBackingFieldReferenceVisitor extends AbstractLenseVisitor{
	
	private String propertyName;
	private FieldOrPropertyAccessNode backingField;

	public ExplicitBackingFieldReferenceVisitor(String propertyName, FieldOrPropertyAccessNode backingField) {
		this.propertyName = propertyName;
		this.backingField = backingField;
	}

	@Override
	protected SemanticContext getSemanticContext() {
		return null;
	}
	
	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		
		if (node instanceof MethodInvocationNode){
			MethodInvocationNode prp = (MethodInvocationNode)node;
			if (prp.isPropertyDerivedMethod() && prp.getPropertyDerivedName().equalsIgnoreCase(propertyName)){
				
				if (prp.getPropertyOperation() == PropertyOperation.READ){
					prp.getParent().replace(prp, backingField);
				} else {
					AssignmentNode assign = new AssignmentNode(Operation.SimpleAssign);
					
					assign.setLeft(backingField);
					assign.setRight((ExpressionNode) prp.getCall().getFirstChild().getFirstChild().getFirstChild());
					prp.getParent().replace(prp, assign);
				}
			}
		}
		
	}



}
