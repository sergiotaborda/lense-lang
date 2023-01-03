package lense.compiler.ast;

import java.util.Optional;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

public class ArgumentListItemNode extends LenseAstNode{

	
	private int index = -1;
	private TypeVariable expectedType;
	private boolean generic;
	private String name;
	private boolean isReificiationArgument =false;
	
	public ArgumentListItemNode(int index , AstNode node){
	    if (node instanceof ArgumentListNode){
	        throw new IllegalArgumentException();
	    }
		this.add(node);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public TypeVariable getExpectedType() {
		return expectedType;
	}
	
	public String toString(){
	    return this.getChildren().get(0).toString();
	}

	public void setExpectedType(TypeVariable expectedType) {
		if (expectedType == null){
			throw new RuntimeException("Type is expected");
		}
		this.expectedType = expectedType;
		
		this.generic = !expectedType.isFixed();
	}
	

	public void setGeneric(boolean generic) {
		this.generic = generic;	
	}
	
	public boolean isGeneric() {
		return this.generic;	
	}

	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	

	public void setName(String name) {
		this.name = name;
	}

	public boolean isReificiationArgument() {
		return isReificiationArgument;
	}

	public void setReificiationArgument(boolean isReificiationArgument) {
		this.isReificiationArgument = isReificiationArgument;
	}

	
}
