/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;


/**
 * 
 */
public class NoneValue extends LiteralExpressionNode {

	public TypeVariable getTypeVariable() {
		return LenseTypeSystem.None();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLiteralValue() {
		return "null";
	}
	
	  public String toString(){
          return  "none";
      }
}
