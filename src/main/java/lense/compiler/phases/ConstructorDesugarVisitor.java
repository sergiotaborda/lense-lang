package lense.compiler.phases;

import java.util.HashSet;
import java.util.Set;

import compiler.parser.IdentifierNode;
import compiler.parser.NameIdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ConstructorExtentionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.VariableReadNode;
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

			if (!c.isPrimary() || c.isNative()) {
				return;
			}
			
			ConstructorExtentionNode extSuper = c.getExtention();
			
			Set<String> ignore = new HashSet<>();
			
			if (extSuper != null) {
				for (AstNode f : extSuper.getArguments().getChildren()) {
					AstNode item = f.getFirstChild();
					
					if (item instanceof NameIdentifierNode) {
						ignore.add(((NameIdentifierNode)item).getName());
					} 
				}
			}
			
			
			ClassBodyNode type = (ClassBodyNode)c.getParent();
			
			for (AstNode f : type.getChildren()) {
			
				if (f instanceof FieldDeclarationNode) {
					ignore.add(((FieldDeclarationNode)f).getName());
				} else 	if (f instanceof PropertyDeclarationNode) {
					ignore.add(((PropertyDeclarationNode)f).getName());
				} 
			}
			
			for(  AstNode a : c.getParameters().getChildren()){
				FormalParameterNode f = (FormalParameterNode)a;

				if (ignore.contains(f.getName())) {
					return;
				}
				Visibility visibility = f.getVisibility();

				if (visibility == Visibility.Undefined){
					visibility = Visibility.Private;
				}

				if (visibility == Visibility.Private){
					// field 
					FieldDeclarationNode fd = new FieldDeclarationNode(f.getName(), f.getTypeNode(), null);
					fd.setVisibility(new VisibilityNode(Visibility.Private));
					fd.setImutability(new ImutabilityNode(f.getImutabilityValue()));

					type.addBefore(node, fd);
	

				} else {
					PropertyDeclarationNode prp = new PropertyDeclarationNode(f.getName(), f.getTypeNode());
					prp.setVisibility(visibility);
					prp.setInicializedOnConstructor(true);

					type.addBefore(node, prp);
					
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


	@Override
	protected SemanticContext getSemanticContext() {
		return semanticContext;
	}



}
