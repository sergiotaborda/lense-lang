package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.context.SemanticContext;

public class JavalizeVisitor implements Visitor<AstNode>{

	private SemanticContext semanticContext;

	public JavalizeVisitor(SemanticContext semanticContext) {
		this.semanticContext = semanticContext;
	}

	@Override
	public void startVisit() {	}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {	
		
		if (node instanceof ArithmeticNode){
			ArithmeticNode n = (ArithmeticNode)node;
			
			MethodInvocationNode m = new MethodInvocationNode(n.getLeft(), n.getOperation().equivalentMethod(), n.getRight());
			n.getParent().replace(n, m);
		}
		
	}

}
