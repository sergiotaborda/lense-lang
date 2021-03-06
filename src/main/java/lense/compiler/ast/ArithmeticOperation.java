/**
 * 
 */
package lense.compiler.ast;

public enum ArithmeticOperation {

        Power ("raiseTo", "^^"),
        WrapMultiplication("wrapMultiply", "&*"),
	    Multiplication ("multiply", "*"),
		Addition("plus", "+"),
		WrapAddition("wrapPlus", "&+"),
		Concatenation("concat", "++"),
		Subtraction("minus","-"),
		WrapSubtraction("wrapMinus","&-"),
		Division("divide","/"),
		Remainder("remainder","%"),
		IntegerDivision("wholeDivide", "\\"),
		RightShift("rightShiftBy", ">>"),
		SignedRightShift("signedRightShiftBy",">>>"),
		LeftShift("leftShiftBy","<<"),

        BitAnd ("and", "&"),
        BitOr ("or", "|"),
        BitXor("xor","^"),
     // unary
        Complement("complement","~"),
        Symmetric("symmetric","-"),
        Positive("positive","+"),
		Increment("successor","++"),
		Decrement("predecessor","--")
		;
		
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