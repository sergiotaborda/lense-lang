package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.ComparisonNode.Operation;

public class OtimizationVisitor implements Visitor<AstNode> {

	@Override
	public void visitAfterChildren(AstNode node) {
//		if (node instanceof AssertNode) {
//			AssertNode r = (AssertNode) node;
//			ExpressionNode val = (ExpressionNode) r.getFirstChild();
//
//			if (!(val instanceof ComparisonNode) && !(val instanceof InstanceOfNode)) {
//				ComparisonNode n = new ComparisonNode(r.getReferenceValue() ? Operation.EqualTo : Operation.Different);
//				n.add(val);
//				n.add(new BooleanValue(r.getReferenceValue()));
//
//				r.replace(val, n);
//			}
//
//		}  
	}
	
	public VisitorNext visitBeforeChildren(AstNode node) {
//		if (node instanceof AssertNode) {
//			AssertNode r = (AssertNode) node;
//			ExpressionNode val = (ExpressionNode) r.getFirstChild();
//
//			if (val instanceof PreBooleanUnaryExpression) {
//				PreBooleanUnaryExpression exp = (PreBooleanUnaryExpression) val;
//
//				if (exp.getOperation() == BooleanOperation.LogicNegate) {
//		
//					AssertNode a = new AssertNode((ExpressionNode) exp.getFirstChild());
//					a.setReferenceValue(false);
//					r.getText().ifPresent(text -> a.setText(text));
//
//					r.getParent().replace(r, a);
//				}
//			}
//
//		} 
		
//		if (node instanceof PreBooleanUnaryExpression) {
//			PreBooleanUnaryExpression p = (PreBooleanUnaryExpression)node;
//			
//			var innerList = p.getChildren(PreBooleanUnaryExpression.class);
//			
//			if(!innerList.isEmpty() && p.getOperation() == BooleanOperation.LogicNegate) {
//				PreBooleanUnaryExpression inner = innerList.get(0);
//				
//				if(inner.getOperation() == BooleanOperation.LogicNegate) {
//					p.getParent().replace(p, inner.getFirstChild());
//					
//				}
//			}
//		}
		
		
		
		return VisitorNext.Children;
	}

}
