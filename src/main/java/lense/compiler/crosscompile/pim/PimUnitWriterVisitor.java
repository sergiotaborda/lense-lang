package lense.compiler.crosscompile.pim;

import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.TypeNode;

public class PimUnitWriterVisitor implements Visitor<AstNode>{

	private final PrintWriter writer;
	private int tab = -1;	
	private int arrayOpen = 0;
	
	public PimUnitWriterVisitor(PrintWriter writer) {
		this.writer = writer;
	}

	@Override
	public VisitorNext visitBeforeChildren(AstNode node) {
		
	
	
		
		if (node instanceof ClassTypeNode other) {
			openObject();
			printProperty("name", other.getFullname());
			printSeparator();
			if (other.getSuperType() != null) {
				printProperty("superType", other.getSuperType().getName());
			} else {
				printProperty("superType", "lense.core.lang.Any");
			}
			printSeparator();
			if (other.getInterfaces() != null && !other.getInterfaces().getChildren().isEmpty()) {
				openArray("implements");
				
				printArray( other.getInterfaces().getChildren(TypeNode.class), n ->{
					
					writer.print(n.getName());
				});
				
				
				closeArray();
				tab++;
				printTabs();
				printSeparator();
			}
	
			writer.println();
		} else if (node instanceof ClassBodyNode other) {
			openArray("members");
			
			printArray( other.getChildren(MethodDeclarationNode.class), method ->{
				openObject();
				printProperty("memberType", "method");
				printSeparator();
				printProperty("name", method.getName());
				printSeparator();
				printProperty("type", method.getReturnType().getName());
				writer.println();
				
				if (!method.getParameters().getChildren().isEmpty()) {
					openArray("parameters");
					
					printArray( method.getParameters().getChildren(FormalParameterNode.class), n ->{
						writer.println("{");
						printProperty("name", n.getName());
						printSeparator();
						printProperty("type", n.getTypeNode().getName());
						writer.println();
						printTabs();
						writer.println("}");
					});
					
					
					closeArray();
					tab++;
				}
				closeObject();
			});
			
			printTabs();
			printSeparator();
			
			printArray( other.getChildren(PropertyDeclarationNode.class), property ->{
				openObject();
				printProperty("memberType", "property");
				printSeparator();
				printProperty("name", property.getName());
				printSeparator();
				printProperty("type", property.getType().getName());
				writer.println();
				closeObject();
			});
			
			printTabs();
			printSeparator();
			
			printArray( other.getChildren(ConstructorDeclarationNode.class), constructor ->{
				openObject();
				printProperty("memberType", "constructor");
				if (constructor.getName() != null) {
					printSeparator();
					printProperty("name", constructor.getName());
				}
				writer.println();
				
				if (!constructor.getParameters().getChildren().isEmpty()) {
					openArray("parameters");
					
					printArray( constructor.getParameters().getChildren(FormalParameterNode.class), n ->{
						writer.println("{");
						printProperty("name", n.getName());
						printSeparator();
						printProperty("type", n.getTypeNode().getName());
						writer.println();
						printTabs();
						writer.println("}");
					});
					
					
					closeArray();
					tab++;
				}
				closeObject();
			});
			
			closeArray();
			
			
		}
		
	
		
		return VisitorNext.Children;
	}
	
	private <T> void printArray(List<T> children, Consumer<T> function) {
		var iterator = children.iterator();
		while ( iterator.hasNext()) {
			var n = iterator.next();
			printTabs();
			function.accept(n);
			tab++;
			printTabs();
			if (iterator.hasNext()) {
				writer.println(",");
			} else {
				writer.println();
			}
			tab--;
		}
	}

	private int arrayItem = 0;
	private void arrayItem() {
		
		if (arrayItem > 0) {
			printTabs();
			writer.println(",");
		}
	
		arrayItem++;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		
		if (node instanceof ClassTypeNode other) {
			closeObject();
		} else if (node instanceof ClassBodyNode other) {
			closeArray();
		} else if (node instanceof ConstructorDeclarationNode other) {
			closeObject();
		}
	

	}
	


	private void printSeparator() {
		writer.println(",");
	}

	private void openObject() {
		tab++;
		printTabs();
		writer.println("{");
		tab++;	
	}
	
	private void closeObject() {
		tab--;
		printTabs();
		writer.println("}");
		tab--;
	}

	
	private void openArray(String name) {
		arrayOpen++; 
		printTabs();
		writer.println("\"" + name + "\":[");
		tab++;
	}
	
	private void closeArray() {
		tab--;
		printTabs();
		writer.println("]");
		arrayOpen--;
		tab--;
	}

	

	private void printTabs() {
		for (int i= 0 ; i< tab ; i++) {
			writer.print("\t");	
		}
	}
	private void printProperty(String name, String value) {
		printTabs();
		 writer.print("\"" + name + "\":\"" + value + "\"");
	}



}
