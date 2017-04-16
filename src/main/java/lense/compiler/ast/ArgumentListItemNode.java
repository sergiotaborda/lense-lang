package lense.compiler.ast;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.TypeVariable;

public class ArgumentListItemNode extends LenseAstNode{

	
	private int index;
	private TypeVariable expectedType;
	private boolean generic;
	
	public ArgumentListItemNode(int index , AstNode node){
		this.add(node);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public TypeVariable getExpectedType() {
		return expectedType;
	}

	public void setExpectedType(TypeVariable expectedType) {
		if (expectedType == null){
			throw new RuntimeException("Type is expected");
		}
		this.expectedType = expectedType;
		
		this.generic = expectedType instanceof IntervalTypeVariable;
	}
	

	public void setGeneric(boolean generic) {
		this.generic = generic;	
	}
	
	public boolean isGeneric() {
		return this.generic;	
	}

	
}
