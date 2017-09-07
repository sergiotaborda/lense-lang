package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.SemanticScope;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

public abstract class AbstractScopedVisitor extends AbstractLenseVisitor  {

	
	private SemanticContext context;

	public AbstractScopedVisitor (SemanticContext context){
		this.context = context;
		
	}
	@Override
	protected SemanticContext getSemanticContext() {
		return context;
	}
	
	protected abstract Optional<LenseTypeDefinition> getCurrentType();
	
    protected IntervalTypeVariable resolveTypeDefinition(TypeNode t) {

        if (t.getTypeParameter() != null){
            return t.getTypeParameter();
        }


        SemanticContext semanticContext = this.getSemanticContext();

        SemanticScope currentScope = semanticContext.currentScope();
        if (currentScope != null){
            VariableInfo variableInfo = currentScope.searchVariable(t.getName());

            if (variableInfo != null && variableInfo.isTypeVariable()){
                // its a generic type parameter
                t.setTypeParameter( new RangeTypeVariable(t.getName(), null, variableInfo.getTypeVariable(), variableInfo.getTypeVariable()));
                return t.getTypeParameter();
            }
        }

        TypeDefinition type = null;
        if (this.getCurrentType().isPresent() && this.getCurrentType().get().getName().equals(t.getName())){
            type = this.getCurrentType().get();
        } else {
            try {
                type = semanticContext.typeForName(t.getName(),t.getTypeParametersCount());
            } catch (lense.compiler.type.TypeNotFoundException e) {
                new CompilationError(t.getParent(), e.getMessage());
            }
        }
       
        if ( t.getTypeParametersCount() > 0) {

            TypeVariable[] genericParametersCapture = new TypeVariable[t.getTypeParametersCount()];
            int index = 0;
            for (AstNode n : t.getChildren()) {
                TypeNode typeNode = ((GenericTypeParameterNode) n).getTypeNode();
                TypeVariable typeVariable;
                if (typeNode!= null){
                    typeVariable =  typeNode.getTypeVariable();
                    if (typeVariable == null) {
                        VariableInfo variableInfo = currentScope.searchVariable(typeNode.getName());
                        if (variableInfo.isTypeVariable()){

                            typeVariable = new GenericTypeBoundToDeclaringTypeVariable(type, currentScope.getCurrentType(), index , typeNode.getName(),  Variance.Covariant);

                        } else {
                            typeVariable = new FixedTypeVariable(semanticContext.typeForName(typeNode.getName(),typeNode.getTypeParametersCount()));
                        }

                    }
                    genericParametersCapture[index] = typeVariable;
                    typeNode.setTypeVariable(genericParametersCapture[index]);
                } else {
                    typeVariable = new FixedTypeVariable( semanticContext.resolveTypeForName("lense.core.lang.Any", 0).get());
                    genericParametersCapture[index] = typeVariable;
                }
                index++;
            }
            type = LenseTypeSystem.specify(type,genericParametersCapture);
            t.setTypeVariable(type);
        }

        t.setTypeVariable(new FixedTypeVariable(type));
        t.setTypeParameter(  t.getTypeVariable().toIntervalTypeVariable());


        return t.getTypeParameter();

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
