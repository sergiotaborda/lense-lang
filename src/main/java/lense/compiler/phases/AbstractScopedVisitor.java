package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.TypeNotFoundError;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
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

	
	protected TypeVariable resolveTypeDefinition(TypeNode t, Variance positionVariance) {

		Optional<TypeVariable> namedType = this.getSemanticContext().resolveTypeForName(t.getName(), t.getTypeParametersCount());

		VariableInfo freeType = this.getSemanticContext().currentScope().searchVariable(t.getName());
		
		TypeVariable type;
		
		// is the type it self
		if (this.getCurrentType().map(c -> c.getName().equals(t.getName())).orElse(false)){
			type = this.getCurrentType().get();
		} 

		// is a parameter of the type
		Optional<Integer> index = this.getCurrentType().flatMap( c -> c.getGenericParameterIndexBySymbol(t.getName()));

		if (index.isPresent()) {
			type = new DeclaringTypeBoundedTypeVariable(this.getCurrentType().get(), index.get(), t.getName(), positionVariance);
		} else if (t.getParent() instanceof FormalParameterNode) {
			// is a parameter of the method ?
			FormalParameterNode f = (FormalParameterNode)t.getParent();
			if (!f.isMethodTypeBound()) {
				
				if (namedType.isPresent()) {
					type = namedType.get();
				} else {
					if (t.getTypeParameter() != null){
						return t.getTypeParameter();
					}
					throw new TypeNotFoundError(t.getParent(),  t.getName() );
				}

			} else {
				type = f.getTypeVariable();
			}
			
		} else {
			if (freeType != null) {

				type = freeType.getTypeVariable();
			} else if (namedType.isPresent()) {
				type = namedType.get();
			} else {
				if (t.getTypeParameter() != null){
					return t.getTypeParameter();
				}
				throw new TypeNotFoundError(t.getParent(),  t.getName());
			}
		}

		


		// it type is parametric
		if (t.getTypeParametersCount() > 0) {

			TypeVariable[] genericParametersCapture = new TypeVariable[t.getTypeParametersCount()];
			int ind = 0;
			for (AstNode n : t.getChildren()) {
				GenericTypeParameterNode genericTypeParameterNode = (GenericTypeParameterNode)n;
				
				TypeNode innerTypeNode = genericTypeParameterNode.getTypeNode();
				
				
				TypeVariable typeVariable = resolveTypeDefinition(innerTypeNode, positionVariance);
				
				genericParametersCapture[ind] = typeVariable;
				innerTypeNode.setTypeVariable(genericParametersCapture[ind]);
				ind++;
			}
			type = type == null ? null : LenseTypeSystem.getInstance().specify(type,genericParametersCapture);

		} 

		t.setTypeVariable(type);
		t.setTypeParameter(t.getTypeVariable());


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
		
		// TODO capture corrent type here
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
