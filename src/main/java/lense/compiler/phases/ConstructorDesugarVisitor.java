package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Visibility;

public class ConstructorDesugarVisitor  extends AbstractLenseVisitor {

	private SemanticContext semanticContext;

	public ConstructorDesugarVisitor(SemanticContext semanticContext) {
		this.semanticContext = semanticContext;
	}

	@Override
	public void startVisit() {}

	@Override
	public void endVisit() {}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		return VisitorNext.Children;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof ConstructorDeclarationNode){
			ConstructorDeclarationNode c = (ConstructorDeclarationNode)node;
			
			ClassBodyNode type = (ClassBodyNode)c.getParent();
			for(  AstNode a : c.getParameters().getChildren()){
				FormalParameterNode f = (FormalParameterNode)a;
				if (f.getVisibility() != Visibility.Undefined){
					if (f.getVisibility() == Visibility.Private){
						// field 
						FieldDeclarationNode fd = new FieldDeclarationNode(f.getName(), f.getTypeNode(), null);
						fd.setVisibility(new VisibilityNode(Visibility.Private));
						fd.setImutability(new ImutabilityNode(f.getImutabilityValue()));
						
						type.add(fd);
						
					} else {
						PropertyDeclarationNode prp = new PropertyDeclarationNode(f.getName(), f.getTypeNode());
						prp.setVisibility(f.getVisibility());
						prp.setInicializedOnConstructor(true);
						type.add(prp);
						
						if (f.getImutabilityValue() == Imutability.Mutable){
							// create get;set property with given Visibility
						
							prp.setAcessor(new AccessorNode(true));
							prp.setModifier(new ModifierNode(true));
							
						} else {
							// create get property with given Visibility
							prp.setAcessor(new AccessorNode(true));

						}
						
						
					}
					
				}
			}
		}
	}

	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}

	

}
