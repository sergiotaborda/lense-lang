package lense.compiler.phases;

import java.util.Optional;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.LenseTypeDefinition;

public class IdentifiersVerifierVisitor extends AbstractScopedVisitor {

	public IdentifiersVerifierVisitor(SemanticContext context) {
		super(context);
	}

	@Override
	protected Optional<LenseTypeDefinition> getCurrentType() {
		return Optional.empty();
	}

	@Override
	protected VisitorNext doVisitBeforeChildren(AstNode node) {
		if (node instanceof IdentifierNode) {
    		validateIdentifier(node, ((IdentifierNode)node).getName());
    	} else  if (node instanceof FieldDeclarationNode) {
    		validateIdentifier(node, ((FieldDeclarationNode)node).getName());
    	} else  if (node instanceof PropertyDeclarationNode) {
    		validateIdentifier(node, ((PropertyDeclarationNode)node).getName());
    	} else  if (node instanceof MethodDeclarationNode) {
    		validateIdentifier(node, ((MethodDeclarationNode)node).getName());
    	} else  if (node instanceof ConstructorDeclarationNode) {
    		validateIdentifier(node, ((ConstructorDeclarationNode)node).getName());
    	}  else  if (node instanceof ClassTypeNode) {
    		validateIdentifier(node, ((ClassTypeNode)node).getSimpleName());
    	} 
		return VisitorNext.Children;
	}

	private void validateIdentifier(AstNode node, String name) {
		if (name == null 
				|| "Package$$Info".equals(name)
				|| "Module$$Info".equals(name) 
				|| name.startsWith("$reificiationInfo$t")
				|| name.endsWith("$$Type")
		) {
			return;
		}
		
		for (char c : name.toCharArray()) {
			if (!(c == '_' || Character.isAlphabetic(c) || Character.isDigit(c))) {
			      throw new CompilationError(node, "Identifier names must start with a letter and may contain _ and numbers. Found:" + name);
			}
		}
		if (name.startsWith("_")
		) {
		      throw new CompilationError(node, "Identifier names must start with a letter and may contain _ and numbers. Found:" + name);
		}
		
	}

	@Override
	protected void doVisitAfterChildren(AstNode node) {
		// no-op
	}

}
