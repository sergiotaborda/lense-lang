/**
 * 
 */
package lense.compiler.ast;

import java.util.List;

import compiler.syntax.AstNode;
import lense.compiler.type.CallableMemberMember;
import lense.compiler.type.Constructor;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class NewInstanceCreationNode extends ExpressionNode{

	private TypeNode typeNode;
	private ArgumentListNode argumentList;
	private String name;
	private Constructor constructor;
	
	public static NewInstanceCreationNode of(TypeVariable left) {
		return new NewInstanceCreationNode(left, new ArgumentListNode());
		
	}
	
	public static NewInstanceCreationNode of(TypeNode left) {
		return new NewInstanceCreationNode(left);
		
	}
	private NewInstanceCreationNode (TypeNode t){
		setTypeNode(t);

		setArguments(new ArgumentListNode());
	}
	
//	public static NewInstanceCreationNode of(TypeVariable left, AstNode param) { // TODO remove
//		ArgumentListItemNode arg = new ArgumentListItemNode(0, param);
//
//		final NewInstanceCreationNode c = new NewInstanceCreationNode(left, ArgumentListNode.of(arg));
//
//		return c;
//
//	}
	
	public static NewInstanceCreationNode of(TypeVariable type, Constructor constructor, AstNode param) {
		ArgumentListItemNode arg = new ArgumentListItemNode(0, param);
		arg.setExpectedType(constructor.getParameters().get(0).getType());
		
		
		final NewInstanceCreationNode c = new NewInstanceCreationNode(type, ArgumentListNode.of(arg));
		c.setConstructor(constructor);
		
		return c;

	}

	public static NewInstanceCreationNode of(TypeVariable type, Constructor constructor,   AstNode ... params) {
		
		ArgumentListNode list = new ArgumentListNode();
		int count=0;
		List<CallableMemberMember<Constructor>> parameters = constructor.getParameters();
		for (AstNode a : params){
			ArgumentListItemNode arg = new ArgumentListItemNode(count, a);
			arg.setExpectedType(parameters.get(count).getType()); 
			list.add(arg);
			count++;
		}

		return new NewInstanceCreationNode(type, list);
	}
	
	public NewInstanceCreationNode (){}
	
//	public NewInstanceCreationNode (TypeVariable type, ArgumentListNode list){
//		TypeNode t = new TypeNode(type);
//		setTypeNode(t);
//		setArguments(list);
//	}
//	
//	public NewInstanceCreationNode (String typeName, ArgumentListNode list){
//		TypeNode t = new TypeNode(new QualifiedNameNode(typeName));
//		setTypeNode(t);
//		setArguments(list);
//	}

	private NewInstanceCreationNode (TypeVariable type, ArgumentListNode args){
		TypeNode t = new TypeNode(type);
		setTypeNode(t);

//		ArgumentListNode list = new ArgumentListNode();
//		
//		int count = 0;
//		for(AstNode s : args){
//			ArgumentListItemNode item = new ArgumentListItemNode(count++, s);
//			
//			list.add(s);
//		}
//		
		setArguments(args);
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
	public TypeVariable getTypeVariable() {
		return typeNode.getTypeVariable();
	}
	
	public void setTypeVariable(TypeVariable type) {
		 super.setTypeVariable(type);
		 typeNode.setTypeVariable(type);
	}


	public void replace(AstNode oldNode , AstNode newNode){
		
		if (oldNode instanceof TypeNode){
			super.replace(this.typeNode, newNode);
			this.typeNode = (TypeNode) newNode;
		} else {
			super.replace(this.typeNode, newNode);
		}
	}

	public void setConstructorName(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}

	public Constructor getConstructor() {
		return constructor;
	}

	public void setConstructor(Constructor constructor) {

		this.constructor = constructor;
		this.setConstructorName(constructor.getName());
	}





}
