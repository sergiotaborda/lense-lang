package lense.compiler.crosscompile;

import lense.compiler.ast.LiteralExpressionNode;
import lense.compiler.type.variable.TypeVariable;

public class PrimitiveStringNode extends LiteralExpressionNode {

	private String value;
	
	public PrimitiveStringNode(){}
	
	public PrimitiveStringNode(String value){
		this.value = value;
	}
	
	public TypeVariable getTypeVariable() {
		return PrimitiveTypeDefinition.STRING;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return value;
	}

	  public String toString(){
          return  value;
      }
}