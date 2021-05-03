package lense.compiler.phases;

import java.util.HashSet;
import java.util.Set;

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

			if (c.isNative()) {
				return;
			}
			
			
			if (c.isPrimary()) {
				visitPrimary(c);
			} else {
				visitNonPrimary(c);
			}
			
			
		}
	}

	private void visitNonPrimary(ConstructorDeclarationNode c) {
//		ConstructorExtentionNode extSuper = c.getExtention();
//		
//		Set<String> ignore = new HashSet<>();
//		
//		if (extSuper != null) {
//			for (AstNode f : extSuper.getArguments().getChildren()) {
//				AstNode item = f.getFirstChild();
//				
//				if (item instanceof NameIdentifierNode) {
//					ignore.add(((NameIdentifierNode)item).getName());
//				} 
//			}
//		}
//		
//		
//		ClassBodyNode type = (ClassBodyNode)c.getParent();
//		
//		for (AstNode f : type.getChildren()) {
//		
//			if (f instanceof FieldDeclarationNode) {
//				ignore.add(((FieldDeclarationNode)f).getName());
//			} else 	if (f instanceof PropertyDeclarationNode) {
//				ignore.add(((PropertyDeclarationNode)f).getName());
//			} 
//		}
		
//		for(  AstNode a : c.getParameters().getChildren()){
//			FormalParameterNode f = (FormalParameterNode)a;
//
//			if (ignore.contains(f.getName())) {
//				return;
//			}
//			Visibility visibility = f.getVisibility();
//
//			if (visibility == Visibility.Undefined){
//				visibility = Visibility.Private; // TODO take the class visibility
//			}
//
//			if (visibility == Visibility.Private){
//				// field 
//				FieldDeclarationNode fd = new FieldDeclarationNode(f.getName(), f.getTypeNode(), null);
//				fd.setVisibility(new VisibilityNode(Visibility.Private));
//				fd.setImutability(new ImutabilityNode(f.getImutabilityValue()));
//				fd.setInitializedOnConstructor(true);
//				
//				type.addBefore(c, fd);
//
//
//			} else {
//				PropertyDeclarationNode prp = new PropertyDeclarationNode(f.getName(), f.getTypeNode());
//				prp.setVisibility(visibility);
//				prp.setInitializedOnConstructor(true);
//
//				type.addBefore(c, prp);
//				
//				if (f.getImutabilityValue() == Imutability.Mutable){
//					// create get;set property with given Visibility
//
//					prp.setAcessor(new AccessorNode(true, true));
//					prp.setModifier(new ModifierNode(true, true));
//
//				} else {
//					// create get property with given Visibility
//					prp.setAcessor(new AccessorNode(true,true));
//
//				}
//
//
//			}
//		}
	}

	private void visitPrimary(ConstructorDeclarationNode c) {
		// read primary fields 
		
		
		if (c.getParameters().getChildren().isEmpty()) {
			return;
		}
		
		Set<String> inPrimary = new HashSet<>();
		
		
		for ( AstNode param : c.getParameters().getChildren()) {
			FormalParameterNode fp = (FormalParameterNode)param;
			inPrimary.add(fp.getName());

		}
		
		ConstructorExtentionNode extSuper = c.getExtention();
		
		if (extSuper != null) {
			for (AstNode f : extSuper.getArguments().getChildren()) {
				AstNode item = f.getFirstChild();
				
				if (item instanceof NameIdentifierNode) {
					inPrimary.remove(((NameIdentifierNode)item).getName());
				} 
			}
		}
		

		ClassBodyNode type = (ClassBodyNode)c.getParent();
		
		for (AstNode f : type.getChildren()) {
		
			if (f instanceof FieldDeclarationNode) {
				FieldDeclarationNode fg = (FieldDeclarationNode)f;
				if (inPrimary.contains(fg.getName())){
					fg.setInitializedOnConstructor(true);
					inPrimary.remove(fg.getName());
				}
		
			} else 	if (f instanceof PropertyDeclarationNode) {
				PropertyDeclarationNode fg = (PropertyDeclarationNode)f;
				if (inPrimary.contains(fg.getName())){
					fg.setInitializedOnConstructor(true);
					inPrimary.remove(fg.getName());
				}
				
			} 
		}
		
		if (!inPrimary.isEmpty()) {
			for ( AstNode param : c.getParameters().getChildren()) {
				FormalParameterNode f = (FormalParameterNode)param;
				if (inPrimary.contains(f.getName())) {
					
					Visibility visibility = f.getVisibility();

					if (visibility == Visibility.Undefined){
						visibility = Visibility.Private; // TODO take the class visibility
					}

					if (visibility == Visibility.Private){
						// field 
						FieldDeclarationNode fd = new FieldDeclarationNode(f.getName(), f.getTypeNode(), null);
						fd.setVisibility(new VisibilityNode(Visibility.Private));
						fd.setImutability(f.getImutabilityValue());
						fd.setInitializedOnConstructor(true);
						
						type.addBefore(c, fd);


					} else {
						PropertyDeclarationNode prp = new PropertyDeclarationNode(f.getName(), f.getTypeNode());
						prp.setVisibility(visibility);
						prp.setInitializedOnConstructor(true);

						type.addBefore(c, prp);
						
						if (f.getImutabilityValue() == Imutability.Mutable){
							// create get;set property with given Visibility

							prp.setAcessor(new AccessorNode(true, true));
							prp.setModifier(new ModifierNode(true, true));

						} else {
							// create get property with given Visibility
							prp.setAcessor(new AccessorNode(true,true));

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
