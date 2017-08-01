package lense.compiler.ast;

import java.util.Optional;

public enum BooleanOperation {
	BitAnd ("&", ArithmeticOperation.BitAnd),
	BitOr ("|", ArithmeticOperation.BitOr),
	BitXor("^", ArithmeticOperation.BitXor),
	BitNegate("~", ArithmeticOperation.Complement), // unary
	LogicNegate("!", null),  // unary
	LogicShortAnd("&&",null),
	LogicShortOr("||",null);
	//InstanceofType("is");
	
	private String symbol;
    private ArithmeticOperation equivalentArithmeticOperation;

	BooleanOperation(String symbol, ArithmeticOperation equivalentArithmeticOperation ){
		this.symbol = symbol;
		this.equivalentArithmeticOperation = equivalentArithmeticOperation;
	}

	public Optional<ArithmeticOperation> equivalentArithmeticOperation(){
	    return Optional.ofNullable(equivalentArithmeticOperation);
	}
	
	/**
	 * @return
	 */
	public String symbol() {
		return symbol;
	}
}