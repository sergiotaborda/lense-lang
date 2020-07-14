package lense.compiler;

import java.io.PrintWriter;

import compiler.parser.IdentifierNode;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.StatementNode;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.typesystem.Visibility;

public class DefinitionsVisitor implements  Visitor<AstNode> {

	private final PrintWriter writer;

	public DefinitionsVisitor(PrintWriter writer) {
		this.writer = writer;
	}
	
	public VisitorNext visitBeforeChildren(AstNode node) {
		if (node instanceof IdentifierNode) {
            String identifier = ((IdentifierNode) node).getName();
            writer.print(identifier);
        } else if (node instanceof QualifiedNameNode) {
            writer.print(((QualifiedNameNode) node).getName());
        } else if (node instanceof BlockNode) {
            writer.println("{");
        } else if (node instanceof ClassTypeNode) {
        	ClassTypeNode t = (ClassTypeNode)node;
        	
        	writer.print(toCode(t.getVisibility()));
        	writer.print(toCode(t.getKind()));
        	
        	writer.print(t.getFullname());
            writer.println("{");
        }  else if (node instanceof MethodDeclarationNode) {
        	MethodDeclarationNode t = (MethodDeclarationNode)node;
        	
        	writer.print(toCode(t.getVisibility()));
        	writer.print(t.getName());
        	
        	write(writer,t.getParameters());
        	
        	writer.println();
        	return VisitorNext.Siblings;
        }  else if (node instanceof ConstructorDeclarationNode) {
        	ConstructorDeclarationNode t = (ConstructorDeclarationNode)node;
        	
        	writer.print(toCode(t.getVisibility()));
        	
        	writer.print("constructor ");
        	
        	if(t.getName()!=null) {
        		writer.print(t.getName());
        	}
        	
        	write(writer,t.getParameters());
        	
        	writer.println();
        	return VisitorNext.Siblings;
        }  else if (node instanceof PropertyDeclarationNode) {
        	PropertyDeclarationNode t = (PropertyDeclarationNode)node;
        	
        	writer.print(toCode(t.getVisibility()));
        	writer.println(t.getName());
        	return VisitorNext.Siblings;
        } else {
        	writer.println(node.getClass().getName());
        }
		
		 return VisitorNext.Children;
	}


	private void write(PrintWriter writer, ParametersListNode parameters) {
		writer.print("(");
		
		var first = true;
		for (var p : parameters.getChildren(FormalParameterNode.class)) {
			
			if(first) {
				first= false;
			} else {
				writer.print(", ");
			}
			writer.print(p.getName());
			writer.print(":");
			writer.print(p.getTypeVariable().getTypeDefinition().getSimpleName());
			
			
			
		}
		
		writer.print(")");
	}

	private String toCode(LenseUnitKind kind) {
		switch(kind) {
		case Annotation:
			return "annotation ";
		case Class:
			return "class ";
		case Enhancement:
			return "enhancement ";
		case Enum:
			return "enum ";
		case Object:
			return "object ";
		case ValueClass:
			return "value class ";
		case Interface:
			return "interface ";
		default:
			return "~~~";
		}
	}

	private String toCode(Visibility visibility) {
		switch(visibility) {
		case Private:
			return "private ";
		case Public:
			return "public ";
		case Protected:
			return "public ";
		default:
			return "";
		}
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		if (node instanceof BlockNode) {
            writer.println("}");
        } else if (node instanceof ClassTypeNode) {
            writer.println("}");
        } else if (node instanceof StatementNode) {
            writer.println(";");
        }
	}

}
