/**
 * 
 */
package lense.compiler.ast;

import compiler.syntax.AstNode;
import compiler.typesystem.TypeDefinition;

/**
 * 
 */
public class ClassInstanceCreation extends ExpressionNode{

	private TypeNode typeNode;
	private ArgumentListNode argumentList;

	public ClassInstanceCreation (){}
	
	public ClassInstanceCreation (TypeDefinition type, ArgumentListNode list){
		TypeNode t = new TypeNode(type);
		setTypeNode(t);
		setArguments(list);
	}
	
	public ClassInstanceCreation (String typeName, ArgumentListNode list){
		TypeNode t = new TypeNode(new QualifiedNameNode(typeName));
		setTypeNode(t);
		setArguments(list);
	}
	
	public ClassInstanceCreation (TypeDefinition type, AstNode ... args){
		TypeNode t = new TypeNode(type);
		setTypeNode(t);

		ArgumentListNode list = new ArgumentListNode();
		
		for(AstNode s : args){
			list.add(s);
		}
		
		setArguments(list);
	}
	
	public ClassInstanceCreation (String typeName, AstNode ... args){
		TypeNode t = new TypeNode(new QualifiedNameNode(typeName));
		setTypeNode(t);

		ArgumentListNode list = new ArgumentListNode();
		
		for(AstNode s : args){
			list.add(s);
		}
		
		setArguments(list);
	}
	
	public ClassInstanceCreation (TypeNode t, AstNode ... args){
		setTypeNode(t);

		ArgumentListNode list = new ArgumentListNode();
		
		for(AstNode s : args){
			list.add(s);
		}
		
		setArguments(list);
	}
	/**
	 * @param typeNode
	 */
	public void setTypeNode(TypeNode type) {
		this.typeNode = type;
		this.add(type);
	}
	
	public TypeNode getTypeNode(){
		return typeNode;
	}

	/**
	 * @param argumentListNode
	 */
	public void setArguments(ArgumentListNode argumentList) {
		this.argumentList = argumentList;
		this.add(argumentList);
	}
	
	/**
	 * Obtains {@link ArgumentListNode}.
	 * @return the argumentList
	 */
	public ArgumentListNode getArguments() {
		return argumentList;
	}

	/**
	 * Obtains {@link TypeNode}.
	 * @return the type
	 */
	public TypeDefinition getTypeDefinition() {
		return typeNode.getTypeDefinition();
	}

	public void replace(AstNode oldNode , AstNode newNode){
		
		if (oldNode instanceof TypeNode){
			super.replace(this.typeNode, newNode);
			this.typeNode = (TypeNode) newNode;
		} else {
			super.replace(this.typeNode, newNode);
		}
	}
	

}
