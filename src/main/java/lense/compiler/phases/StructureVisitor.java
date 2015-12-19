/**
 * 
 */
package lense.compiler.phases;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lense.compiler.ast.AnnotationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.typesystem.Kind;
import lense.compiler.typesystem.LenseTypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.CompilationError;
import lense.compiler.SemanticContext;
import lense.compiler.Visibility;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import compiler.typesystem.MethodParameter;
import compiler.typesystem.MethodSignature;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;

/**
 * Read the classe members and fills a SenseType object
 */
public class StructureVisitor implements Visitor<AstNode> {


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
			
			currentType.addField(f.getName(), f.getTypeNode().getTypeDefinition(), f.getImutabilityValue());

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


	

			resolveTypeDefinition(m.getReturnType());

			for (AstNode p :  m.getParameters().getChildren()){
				FormalParameterNode f = (FormalParameterNode)p;
				resolveTypeDefinition(f.getTypeNode());
			}

			TypeDefinition returnType = m.getReturnType().getTypeDefinition();

			markDefine(currentType, m.getName(), returnType, m.getParameters());

		} else if (node instanceof TypeNode) {
			resolveTypeDefinition((TypeNode)node);
		}

	}

//	private TypeDefinition resolveFromTypeNode(TypeNode v){
//		return semanticContext.resolveTypeForName(v.getName(), v.getGenericParametersCount()).get();
//	}

	/**
	 * @param name
	 * @param parameters
	 */
	private void markDefine(LenseTypeDefinition declaringType, String name, TypeDefinition returnType, ParametersListNode parameters) {


		MethodParameter[] params = parameters == null 
				? new MethodParameter[0]
				: new MethodParameter[parameters.getChildren().size()];

		for (int i = 0; i < params.length; i++) {
					FormalParameterNode var = (FormalParameterNode) parameters.getChildren().get(i);
					params[i] = new MethodParameter(var.getTypeDefinition());
		}

		declaringType.addMethod(name, returnType,params);


	}

	private void resolveTypeDefinition(TypeNode t) {
		try {
			TypeDefinition type = semanticContext.typeForName(t.getName(), t.getGenericParametersCount());
			t.setTypeDefinition(type);

			if (t.getGenericParametersCount() > 0){
				TypeDefinition[] genericParametersCapture = new TypeDefinition[t.getGenericParametersCount()];
				int index=0;
				for( AstNode n : t.getChildren()){
					GenericTypeParameterNode p = (GenericTypeParameterNode)n;
					TypeNode gt = p.getTypeNode();
					TypeDefinition gtype  = gt.getTypeDefinition();
					if (gtype == null){
						gtype = semanticContext.typeForName(gt.getName(), gt.getGenericParametersCount());
					}
					genericParametersCapture[index] = gtype;
					gt.setTypeDefinition(genericParametersCapture[index]);
					index++;
				}
				t.setTypeDefinition(LenseTypeSystem.specify(type, genericParametersCapture));
			}

		} catch (compiler.typesystem.TypeNotFoundException e) {
			throw new CompilationError(t, e.getMessage());
		}
	}
}
