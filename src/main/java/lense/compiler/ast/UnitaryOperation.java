package lense.compiler.ast;

public enum UnitaryOperation {

	Increment (ArithmeticOperation.Increment),
	Decrement(ArithmeticOperation.Decrement),
	Symmetric (ArithmeticOperation.Symmetric),
	Positive (ArithmeticOperation.Positive),
	Complement (ArithmeticOperation.Complement)
	;
	

	private final ArithmeticOperation arithmeticOperation;

	private UnitaryOperation(ArithmeticOperation arithmeticOperation) {
		this.arithmeticOperation = arithmeticOperation;
	}
	
	public ArithmeticOperation getArithmeticOperation() {
		return arithmeticOperation;
	}
	
}
