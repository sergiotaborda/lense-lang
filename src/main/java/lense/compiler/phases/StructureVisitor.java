/**
 * 
 */
package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeMemberDeclaringTypeVariable;
import lense.compiler.type.variable.TypeVariable;
/**
 * Read the classe members and fills a SenseType object
 */
public class StructureVisitor extends AbstractLenseVisitor {


	private LenseTypeDefinition currentType;
	private SemanticContext semanticContext;

	public StructureVisitor (LenseTypeDefinition currentType, SemanticContext semanticContext){
		this.currentType = currentType;
		this.semanticContext = semanticContext;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {}

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

		if (node instanceof ConstructorDeclarationNode){
			ConstructorDeclarationNode f = (ConstructorDeclarationNode)node;

			ConstructorParameter[] params = asConstructorParameters(f.getParameters());
			
			currentType.addConstructor(f.isImplicit(), f.getName(), params);

		} else 	if (node instanceof FieldDeclarationNode){
			FieldDeclarationNode f = (FieldDeclarationNode)node;

			resolveTypeDefinition(f.getTypeNode());

			currentType.addField(f.getName(), f.getTypeNode().getTypeVariable(), f.getImutabilityValue());

		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;

			//			TypeDefinition returnType = resolveFromTypeNode(m.getReturnType());
			//
			//			// TODO Return Type and Param types can be generic
			//			if (m.getParameters() == null || m.getParameters().getChildren().isEmpty()){
			//
			//				currentType.addMethod(m.getName(),returnType);
			//			} else {
			//				MethodParameter[] parameters  = new MethodParameter[m.getParameters().getChildren().size()];
			//
			//				int i = 0;
			//				for ( AstNode p : m.getParameters().getChildren()){
			//					FormalParameterNode v = (FormalParameterNode)p;
			//
			//					parameters[i++] = new MethodParameter(resolveFromTypeNode(v.getTypeNode()), v.getName() );
			//				}
			//
			//				currentType.addMethod(m.getName(), returnType, parameters);
			//			}

			IntervalTypeVariable typeParameter = resolveTypeDefinition(m.getReturnType());

			lense.compiler.type.variable.TypeVariable returnTypeVariable;
			if (typeParameter.getLowerBound().equals(typeParameter.getUpperbound())){
				returnTypeVariable = typeParameter;
			} else {
				String typeName = m.getReturnType().getName();
				Optional<Integer> opIndex = currentType.getGenericParameterIndexBySymbol(typeName);
				
				
				if (!opIndex.isPresent()){
					throw new CompilationError( m.getReturnType(), typeName + " is not a generic type parameter in type " + currentType.getName());
				}

				returnTypeVariable = new TypeMemberDeclaringTypeVariable(null, opIndex.get());
			}

		
			ParametersListNode parameters = m.getParameters();
			MethodParameter[] params = asMethodParameters(parameters);

			Method method = new Method(m.getName(), new MethodReturn(returnTypeVariable), params);
			currentType.addMethod(method);
		} else if (node instanceof PropertyDeclarationNode){
			PropertyDeclarationNode p = (PropertyDeclarationNode)node;

			String typeName = p.getType().getName();
			VariableInfo genericParameter = semanticContext.currentScope().searchVariable(typeName);
			
			if (genericParameter != null && genericParameter.isTypeVariable()){

				Optional<Integer> index =currentType.getGenericParameterIndexBySymbol(typeName);
				if (!index.isPresent()){
					throw new CompilationError(node, typeName + " is not a valid type or generic parameter");
				}
				
				TypeVariable pp = new TypeMemberDeclaringTypeVariable(null, index.get());
				
				if (p.isIndexed()){
					 lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					 int i =0;
					 for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeVariable();
					}
					 
					currentType.addIndexer(pp, p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), pp, p.getAcessor() != null, p.getModifier() != null);
				}
				
			} else {
				if (p.isIndexed()){
					 lense.compiler.type.variable.TypeVariable[] params = new  lense.compiler.type.variable.TypeVariable[((IndexerPropertyDeclarationNode)p).getIndexes().getChildren().size()];
					 int i =0;
					 for (AstNode n :  ((IndexerPropertyDeclarationNode)p).getIndexes().getChildren()) {
						FormalParameterNode var = (FormalParameterNode) n;
						params[i++] = var.getTypeNode().getTypeVariable();
					}
					 
					currentType.addIndexer(p.getType().getTypeVariable(), p.getAcessor() != null, p.getModifier() != null, params);
				} else {
					currentType.addProperty(p.getName(), p.getType().getTypeVariable(), p.getAcessor() != null, p.getModifier() != null);
				}
			}
		} else if (node instanceof TypeNode) {
			resolveTypeDefinition((TypeNode)node);
		}

	}

	private ConstructorParameter[] asConstructorParameters(ParametersListNode parameters) {
		MethodParameter[] params = asMethodParameters(parameters);
		ConstructorParameter[] cparams = new ConstructorParameter[params.length];
		
		for (int i =0; i < params.length; i++){
			cparams[i] = new ConstructorParameter(params[i].getType(), params[i].getName());
		}
		
		return cparams;
	}
	
	private MethodParameter[] asMethodParameters(ParametersListNode parameters) {
		
		for (AstNode p : parameters.getChildren()){
			FormalParameterNode f = (FormalParameterNode)p;
			resolveTypeDefinition(f.getTypeNode());
		}

		
		MethodParameter[] params = (parameters == null) ? new MethodParameter[0] : new MethodParameter[parameters.getChildren().size()];

		for (int i = 0; i < params.length; i++) {
			FormalParameterNode var = (FormalParameterNode) parameters.getChildren().get(i);
			if (var.getTypeVariable() == null){
				
				Optional<Integer> opIndex = var.getTypeNode().getTypeParameter().getSymbol().flatMap(s -> currentType.getGenericParameterIndexBySymbol(s));
				
			
				if (!opIndex.isPresent()){
					throw new CompilationError(parameters, var.getTypeNode().getTypeParameter().getSymbol() + " is not a generic type parameter in type " + currentType.getName());
				}
				lense.compiler.type.variable.TypeVariable tv = new TypeMemberDeclaringTypeVariable(null, opIndex.get());
				
				params[i] = new MethodParameter(tv, var.getName());
			} else {
				params[i] = new MethodParameter(var.getTypeVariable(), var.getName());
			}
			
		}
		return params;
	}



	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}


}
