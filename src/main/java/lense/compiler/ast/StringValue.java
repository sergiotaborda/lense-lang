/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class StringValue extends LiteralExpressionNode {

	private String value;
	
	public StringValue(){}
	
	public StringValue(String value){
		this.value = value;
	}
	
	public TypeVariable getTypeVariable() {
		return new FixedTypeVariable(LenseTypeSystem.String());
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
