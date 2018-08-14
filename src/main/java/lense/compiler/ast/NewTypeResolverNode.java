package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

public final class NewTypeResolverNode extends AbstractTypeResolverNode {

	
	private TypeVariable resolvingType;

	public NewTypeResolverNode(TypeVariable type) {
		this.resolvingType = type;
	}
	
	public NewTypeResolverNode(TypeVariable type, AstNode ... parameters) {
		this.resolvingType = type;
		
		for (AstNode a : parameters) {
			this.add(a);
		}

	}

	
	public String getTypeName() {
		return resolvingType.getTypeDefinition().getName();
	}

	public boolean hasParameters() {
		return !this.getChildren().isEmpty();
	}
}
