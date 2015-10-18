/**
 * 
 */
package lense.compiler;

import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.typesystem.LenseTypeDefinition;
import lense.compiler.SemanticContext;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import compiler.typesystem.MethodParameter;

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
		
		if (node instanceof TypeNode){
			TypeNode t = (TypeNode)node;
			t.setTypeDefinition( semanticContext.typeForName(t.getName(), t.getGenericParametersCount()));
		} else if (node instanceof FieldDeclarationNode){
			FieldDeclarationNode f = (FieldDeclarationNode)node;
			currentType.addField(f.getName(), f.getTypeDefinition(), f.getImutabilityValue());
		
		} else if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;
			
			// TODO Return Type and Param types can be generic
			if (m.getParameters() == null || m.getParameters().getChildren().isEmpty()){
				
				currentType.addMethod(m.getName(), m.getReturnType().getTypeDefinition());
			} else {
				MethodParameter[] parameters  = new MethodParameter[m.getParameters().getChildren().size()];
				
				int i = 0;
				for ( AstNode p : m.getParameters().getChildren()){
					VariableDeclarationNode v = (VariableDeclarationNode)p;
					
					parameters[i++] = new MethodParameter(v.getTypeDefinition(), v.getName() );
				}
				
				currentType.addMethod(m.getName(), m.getReturnType().getTypeDefinition(), parameters);
			}
			
			
		} else {
			System.out.println("Visiting : " + node.getClass());
		}
		
	}
}
