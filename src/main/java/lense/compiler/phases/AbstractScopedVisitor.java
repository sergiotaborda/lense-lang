package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.context.SemanticContext;

public abstract class AbstractScopedVisitor extends AbstractLenseVisitor  {

	
	private SemanticContext context;

	public AbstractScopedVisitor (SemanticContext context){
		this.context = context;
		
	}
	@Override
	protected SemanticContext getSemanticContext() {
		return context;
	}

	
	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	/**
	 * {@inheritDoc}
	 */
	public final VisitorNext visitBeforeChildren(AstNode node) {
			if (node instanceof ScopeDelimiter){
				ScopeDelimiter scopeDelimiter = (ScopeDelimiter) node;
				this.getSemanticContext().beginScope(scopeDelimiter.getScopeName());
				
			}
			return doVisitBeforeChildren(node);
		
	}
	

	@Override
	public final void visitAfterChildren(AstNode node) {
		try {
		    doVisitAfterChildren(node);
		} finally {
			if (node instanceof ScopeDelimiter){
				this.getSemanticContext().endScope();
			}
		}
	}

	protected abstract VisitorNext doVisitBeforeChildren(AstNode node);
	protected abstract void doVisitAfterChildren(AstNode node);
	
	


}
