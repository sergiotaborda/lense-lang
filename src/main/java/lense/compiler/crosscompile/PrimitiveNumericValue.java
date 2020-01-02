package lense.compiler.crosscompile;

import java.math.BigDecimal;

import lense.compiler.ast.NumericValue;
import lense.compiler.type.variable.TypeVariable;


public class PrimitiveNumericValue extends NumericValue {

    private PrimitiveTypeDefinition type;
    
    public PrimitiveNumericValue(PrimitiveTypeDefinition type , NumericValue other) {
	    this.type = type;
	    this.setValue((BigDecimal)other.getValue(), type);
	}
	
	public TypeVariable getTypeVariable() {
		return type;
	}

}
