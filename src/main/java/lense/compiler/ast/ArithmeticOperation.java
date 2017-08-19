/**
 * 
 */
package lense.compiler.ast;

public enum ArithmeticOperation {

        Power ("raiseTo", "**"),
	    Multiplication ("multiply", "*"),
		Addition("plus", "+"),
		Subtraction("minus","-"),
		Division("divide","/"),
		Remainder("remainder","%"),
		IntegerDivision("wholeDivide", "\\"),
		RightShift("rightShift", ">>"),
		SignedRightShift("signedRightShift",">>>"),
		LeftShift("leftShift","<<"),
		Increment("successor","++"),
		Decrement("predecessor","--"),
        BitAnd ("and", "&"),
        BitOr ("or", "|"),
        BitXor("xor","^"),
        Complement("flipAll","~"); // unary
    
		private String equivalentMethod;
		private String symbol;
		
		ArithmeticOperation(String equivalentMethod, String symbol){
			this.equivalentMethod = equivalentMethod;
			this.symbol = symbol;
		}
		
		public String equivalentMethod(){
			return equivalentMethod;
		}
		
		public String symbol(){
			return symbol;
		}
}