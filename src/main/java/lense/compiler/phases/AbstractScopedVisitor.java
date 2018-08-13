package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
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

		if (t.getTypeParameter() != null){
			return t.getTypeParameter();
		}
		
		Optional<TypeVariable> namedType = this.getSemanticContext().resolveTypeForName(t.getName(), t.getTypeParametersCount());

		TypeVariable type;
		if (namedType.isPresent()) {
			type = namedType.get();
		} else {
			// if is not a named type, then is a parameter


			if (this.getCurrentType().get().getName().equals(t.getName())) {
				type = this.getCurrentType().get();
			} 

			Optional<Integer> index = this.getCurrentType().get().getGenericParameterIndexBySymbol(t.getName());

			if (index.isPresent()) {
				type = new DeclaringTypeBoundedTypeVariable(this.getCurrentType().get(), index.get(), t.getName(), positionVariance);
			} else if (t.getParent() instanceof FormalParameterNode) {
				
				FormalParameterNode f = (FormalParameterNode)t.getParent();
				if (!f.isMethodTypeBound()) {
					throw new CompilationError(t, "Type "  + t.getName() + " is not recognized. Did you imported it?");
				}
				
				type = f.getTypeVariable();
			} else {
				throw new CompilationError(t, "Type "  + t.getName() + " is not recognized. Did you imported it?");
			}

		}


		// it type is parametric
		if (t.getTypeParametersCount() > 0) {

			TypeVariable[] genericParametersCapture = new TypeVariable[t.getTypeParametersCount()];
			int index = 0;
			for (AstNode n : t.getChildren()) {
				GenericTypeParameterNode genericTypeParameterNode = (GenericTypeParameterNode)n;
				
				TypeNode innerTypeNode = genericTypeParameterNode.getTypeNode();
				
				
				TypeVariable typeVariable = resolveTypeDefinition(innerTypeNode, positionVariance);
				
				
//				if (innerTypeNode!= null){
//
//					//					typeVariable = resolveTypeDefinition(innerTypeNode, positionVariance);
//					//					if (typeVariable == null) {
//					typeVariable =  innerTypeNode.getTypeVariable();
//					if (typeVariable == null) {
//						
//						// possible composed generic like Array<Maybe<T>>
//						Optional<TypeVariable> innerType = this.getSemanticContext().resolveTypeForName(innerTypeNode.getName(), innerTypeNode.getTypeParametersCount());
//
//						if (innerType.isPresent()) {
//
//							if (innerType.get().getGenericParameters().isEmpty()) {
//								typeVariable = LenseTypeSystem.specify(type,innerType.get());
//							} else {
//
//								Optional<Integer> opIndex = ((LenseTypeDefinition)currentScope.getCurrentType()).getGenericParameterIndexBySymbol(innerType.get().getGenericParameters().get(0).getSymbol().get());
//
//								// handle composition Array<Maybe<T>> 
//								typeVariable = new GenericTypeBoundToDeclaringTypeVariable(innerType.get().getTypeDefinition(), currentScope.getCurrentType(), opIndex.get() , innerTypeNode.getName(),  Variance.Covariant);
//							}
//
//						} else {
//							throw new CompilationError(t, "Type " +  innerTypeNode.getName() + " is not recognized");
//						}
//						
////						VariableInfo variableInfo = currentScope.searchVariable(innerTypeNode.getName());
////						if (variableInfo == null) {
////							
////
////						} else if (variableInfo.isTypeVariable()){
////							typeVariable = new DeclaringTypeBoundedTypeVariable(currentScope.getCurrentType(), index , innerTypeNode.getName(),  Variance.Covariant);
////
////						} else {
////							typeVariable = semanticContext.typeForName(innerTypeNode);
////						}
////
////						//		}
//					} 
//
//					genericParametersCapture[index] = typeVariable;
//					innerTypeNode.setTypeVariable(genericParametersCapture[index]);
//				} else {
//					typeVariable = semanticContext.resolveTypeForName("lense.core.lang.Any", 0).get();
//					genericParametersCapture[index] = typeVariable;
//				}
				
				genericParametersCapture[index] = typeVariable;
				innerTypeNode.setTypeVariable(genericParametersCapture[index]);
				index++;
			}
			type = type == null ? null : LenseTypeSystem.specify(type,genericParametersCapture);

		} 

//		if (type == null) {
//
//			for ( TypeVariable p : this.getCurrentType().get().getGenericParameters()) {
//				if (p.getSymbol().map( s -> s.equals(t.getName())).orElse(false)) {
//					type = p;
//					break;
//				}
//			}
//		}
//
//		if (type == null) {
//			try {
//				type = semanticContext.typeForName(t);
//			} catch (lense.compiler.type.TypeNotFoundException e) {
//				throw new CompilationError(t.getParent(), e.getMessage());
//			}
//		}



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
