/**
 * 
 */
package lense.compiler.ast;

public enum ArithmeticOperation {

	    Multiplication ("multiply", "*"),
		Addition("plus", "+"),
		Subtraction("minus","-"),
		Division("divide","/"),
		Remainder("remainder","%"),
		FractionDivision("rationalDivide", "//"),
		RightShift("rightShift", ">>"),
		SignedRightShift("signedRightShift",">>>"),
		LeftShift("leftShift","<<"),
		Increment("increment","++"),
		Decrement("decrement","--");
		
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