/**
 * 
 */
package lense.compiler.phases;

import java.util.List;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeMemberDeclaringTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;

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

		if (node instanceof FieldDeclarationNode){
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
				int index = resolveCurrentTypeGenericTypeParameterIndex(m.getReturnType().getName());
				returnTypeVariable = new TypeMemberDeclaringTypeVariable(index);
			}

			for (AstNode p :  m.getParameters().getChildren()){
				FormalParameterNode f = (FormalParameterNode)p;
				resolveTypeDefinition(f.getTypeNode());
			}

			ParametersListNode parameters = m.getParameters();
			MethodParameter[] params = (parameters == null) ? new MethodParameter[0] : new MethodParameter[parameters.getChildren().size()];

			for (int i = 0; i < params.length; i++) {
				FormalParameterNode var = (FormalParameterNode) parameters.getChildren().get(i);
				if (var.getTypeVariable() == null){
					
					int index = resolveCurrentTypeGenericTypeParameterIndex(var.getTypeNode().getTypeParameter().getName());
					lense.compiler.type.variable.TypeVariable tv = new TypeMemberDeclaringTypeVariable(index);
					
					params[i] = new MethodParameter(tv, var.getName());
				} else {
					params[i] = new MethodParameter(var.getTypeVariable(), var.getName());
				}
				
			}

			Method method = new Method(m.getName(), new MethodReturn(returnTypeVariable), params);
			currentType.addMethod(method);
		} else if (node instanceof PropertyDeclarationNode){
			PropertyDeclarationNode p = (PropertyDeclarationNode)node;

			String typeName = p.getType().getName();
			VariableInfo genericParameter = semanticContext.currentScope().searchVariable(typeName);
			
			if (genericParameter != null && genericParameter.isTypeVariable()){
				List<IntervalTypeVariable> parameters = currentType.getGenericParameters();
				TypeMemberDeclaringTypeVariable pp= null;
				for (int i =0; i < parameters.size(); i++){
					if (parameters.get(i).getName().equals(typeName)){
						pp = new TypeMemberDeclaringTypeVariable (i);
						break;
					}
				}
				
				if (pp == null){
					throw new CompilationError(node, "Generic type parameter " + typeName + " is not defined");
				}

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


	private int resolveCurrentTypeGenericTypeParameterIndex(String name) {
		int index = -1;
		int i = 0;
		for ( IntervalTypeVariable param : currentType.getGenericParameters()){
			if (param.getName().equals(name)){
				index = i;
				break;
			}
			i++;
		}
		return index;
	}

	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}


}
