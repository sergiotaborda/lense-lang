package lense.compiler.phases;

import java.util.List;
import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.CaptureReifiedTypesNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.ReceiveReifiedTypesNodes;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.Visibility;

public class ReificationVisitor extends AbstractScopedVisitor {


	private LenseTypeDefinition currentType;

	public ReificationVisitor (SemanticContext semanticContext) {
		super(semanticContext);
	}
	

	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		return Optional.of(currentType);
	}

	@Override
	public VisitorNext doVisitBeforeChildren(AstNode node) {
		
		if (node instanceof ClassTypeNode) {
			 
			this.currentType =	((ClassTypeNode)node).getTypeDefinition();
			
		} 
		
		return VisitorNext.Children;
	}

	@Override
	public void doVisitAfterChildren(AstNode node) {

		if (node instanceof ConstructorDeclarationNode) {
			
			ConstructorDeclarationNode n = (ConstructorDeclarationNode)node;
			
			List<IntervalTypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
			if (!genericParameters.isEmpty()) {
				n.getParameters().addFirst(new ReceiveReifiedTypesNodes());
			}
			
		} else if (node instanceof NewInstanceCreationNode) {
			NewInstanceCreationNode n = (NewInstanceCreationNode)node;
			
			if (!n.getTypeVariable().getGenericParameters().isEmpty()) {
				if (n.getArguments() == null) {
					n.setArguments(new ArgumentListNode(new CaptureReifiedTypesNode()));
				} else {
					n.getArguments().addFirst(new CaptureReifiedTypesNode());
				}
				
			}
		} else if (node instanceof ClassTypeNode) {
			 
			ClassTypeNode n = (ClassTypeNode)node;
			
			if (n.getKind() == LenseUnitKind.Class) {
				List<IntervalTypeVariable> genericParameters = getCurrentType().get().getGenericParameters();
				if (!genericParameters.isEmpty()) {
					
					FieldDeclarationNode field = new FieldDeclarationNode("_reificiationInfo", new TypeNode("lense.core.lang.reflection.ReifiedArguments"));
					field.setInitializedOnConstructor(true);
					field.setVisibility(new VisibilityNode(Visibility.Private));
					field.setImutability(new ImutabilityNode(Imutability.Imutable));
					
					n.getBody().add(field);
				}
			}
			
		} 
	}





}
