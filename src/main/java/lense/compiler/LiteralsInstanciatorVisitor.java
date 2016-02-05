/**
 * 
 */
package lense.compiler;

import compiler.trees.Visitor;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.ClassInstanceCreationNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NullValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StringConcatenationNode;
import lense.compiler.ast.StringValue;
import lense.compiler.typesystem.LenseTypeSystem;
import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;

/**
 * 
 */
public class LiteralsInstanciatorVisitor implements Visitor<AstNode> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitAfterChildren(AstNode node) {
		
		if (node instanceof AssignmentNode){
			AssignmentNode n = (AssignmentNode) node;
			if (n.getRight() instanceof LiteralExpressionNode){
				n.setRight(transformeLiteral((LiteralExpressionNode)n.getRight()));
			}
		} else if (node instanceof ScopedVariableDefinitionNode){
			ScopedVariableDefinitionNode v = (ScopedVariableDefinitionNode)node;
			
			if (v.getInitializer() instanceof LiteralExpressionNode){
				v.setInitializer(transformeLiteral((LiteralExpressionNode)v.getInitializer()));
			}
			
		} 
		if (node instanceof LiteralExpressionNode){
			AstNode newnode = transformeLiteral((LiteralExpressionNode) node);
			
			if (node.getParent().getParent() instanceof ClassInstanceCreationNode){
				ClassInstanceCreationNode c = (ClassInstanceCreationNode)node.getParent().getParent();
				if (c.getTypeDefinition().equals(LenseTypeSystem.String())){
					return;
				}
			} 
			node.getParent().replace(node, newnode);
		}
		else if (node instanceof ArithmeticNode){
			ArithmeticNode a = (ArithmeticNode) node;
			if (a.getTypeDefinition().getName().equals("sense.String")){
				StringConcatenationNode c;
				if (a.getLeft() instanceof StringConcatenationNode){
					c = (StringConcatenationNode)a.getLeft();
					c.add(a.getRight());
				} else {
					c= new StringConcatenationNode();
					c.add(a.getLeft());
					c.add(a.getRight());
					
				}
				a.getParent().replace(a, c);		
				
			}
		}else if (node instanceof MethodInvocationNode){
			MethodInvocationNode m = (MethodInvocationNode) node;
			if (m.getAccess() instanceof NumericValue){
				AstNode newnode = transformeLiteral((LiteralExpressionNode) m.getAccess());
				m.replace(m.getAccess(), newnode);
			}
		}
	}

	private ExpressionNode transformeLiteral(LiteralExpressionNode literal) {
	
		if (literal instanceof BooleanValue){
			return literal;
			//new FieldAccessNode(((BooleanValue)literal).isValue()? "True" : "False");
		} else if (literal instanceof NullValue){
			FieldOrPropertyAccessNode n = new FieldOrPropertyAccessNode("None.None");
			n.setTypeDefinition(LenseTypeSystem.None());
			return n;
		} else if (literal instanceof NumericValue){
			return new ClassInstanceCreationNode(literal.getTypeDefinition(), 
					new ClassInstanceCreationNode(LenseTypeSystem.String(), new StringValue(literal.getLiteralValue())));
		} else {
			return new ClassInstanceCreationNode(literal.getTypeDefinition(), new StringValue(literal.getLiteralValue()));
		}
	}




}
