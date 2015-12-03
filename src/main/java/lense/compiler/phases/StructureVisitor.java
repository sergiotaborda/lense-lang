/**
 * 
 */
package lense.compiler.phases;

import java.util.Optional;

import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.typesystem.LenseTypeDefinition;
import lense.compiler.SemanticContext;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import compiler.typesystem.MethodParameter;
import compiler.typesystem.TypeDefinition;

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
			
			currentType.addField(f.getName(), resolveFromTypeNode(f.getTypeNode()), f.getImutabilityValue());
		
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;
			
			TypeDefinition returnType = resolveFromTypeNode(m.getReturnType());
			
			// TODO Return Type and Param types can be generic
			if (m.getParameters() == null || m.getParameters().getChildren().isEmpty()){
				
				currentType.addMethod(m.getName(),returnType);
			} else {
				MethodParameter[] parameters  = new MethodParameter[m.getParameters().getChildren().size()];
				
				int i = 0;
				for ( AstNode p : m.getParameters().getChildren()){
					FormalParameterNode v = (FormalParameterNode)p;
					
					parameters[i++] = new MethodParameter(resolveFromTypeNode(v.getTypeNode()), v.getName() );
				}
				
				currentType.addMethod(m.getName(), returnType, parameters);
			}
		} 
		
	}
	
	private TypeDefinition resolveFromTypeNode(TypeNode v){
		return semanticContext.resolveTypeForName(v.getName(), v.getGenericParametersCount()).get();
		
	}
}
