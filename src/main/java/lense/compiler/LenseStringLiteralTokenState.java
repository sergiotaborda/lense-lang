/**
 * 
 */
package lense.compiler;

import java.util.Optional;
import java.util.function.Consumer;

import compiler.Grammar;
import compiler.lexer.ParseState;
import compiler.lexer.ScanPosition;
import compiler.lexer.StringLiteralTokenState;
import compiler.lexer.Token;
import compiler.lexer.TokenState;

/**
 * 
 */
public class LenseStringLiteralTokenState extends StringLiteralTokenState {
	
	ScanPosition interpolationPositon;
	StringBuilder interpolated = new StringBuilder();
	boolean readInterpolation = false;
	boolean mayInterpolate;
	boolean mayUnicode;
	boolean escape;
	boolean bindToNext = false;
	/**
	 * Constructor.
	 * @param currentState
	 */
	public LenseStringLiteralTokenState(TokenState currentState) {
		super(currentState);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ParseState receive(ScanPosition pos, char c,Consumer<Token> tokensQueue) {
		final Grammar grammar = this.getScanner().getGrammar();
		if (grammar.isStringLiteralDelimiter(c)){
			
			if (escape){
				// escaped string delimiter
				// just added to the string
				builder.append(c);
				escape = false; // reset flag
			} else {
			    
			    String endTerm = builder.toString();
			    
                Optional<Token> token = grammar.stringLiteralMath(pos, endTerm);
                if (token.isPresent()){
                    tokensQueue.accept(token.get());
                }
		    

				return this.getScanner().newInitialState();
			}
			
		} else if (c == '\\'){
			escape = true;
		} else if (c == '{'){
			
			if (escape){
				// found \{
				mayUnicode= true;
				interpolationPositon = pos;
				readInterpolation = true;
			} else if (!mayInterpolate){
				// found {
				mayInterpolate = true;
			} else if (mayInterpolate){
				// found {{
				interpolationPositon = pos;
				readInterpolation = true;
			}
			
		} else if (c == '}'){
			if (mayUnicode){
				mayUnicode = false;
				readInterpolation = false;
				
				String code = interpolated.toString();
				interpolated = new StringBuilder();
				
				if (code.charAt(0) != '#'){
					throw new CompilationError("Expected hexadecimal value for unicode character");
				}
				
				builder.append("\\u" + code.substring(1));
				
			} else if (readInterpolation && mayInterpolate){
				mayInterpolate = false;
			} else if (readInterpolation){
				readInterpolation = false;
				
				String insideExpression = interpolated.toString().trim();
				interpolated = new StringBuilder();
				
				Optional<Token> token = grammar.stringLiteralMath(pos, builder.toString());
				if (token.isPresent()){
					tokensQueue.accept(token.get());
					builder.delete(0, builder.length());
					builder.append(" ");
				}
				
//				// send . operator
//				tokensQueue.accept(grammar.maybeMatch(pos, ".").get());
//				
//				// send plus method 
//				tokensQueue.accept(grammar.maybeMatch(pos, "plus").get());
				
		        // send ++ operator
                tokensQueue.accept(grammar.maybeMatch(pos, "++").get());
				
				// send ( operator
				tokensQueue.accept(grammar.maybeMatch(pos, "(").get());
				
				// re-scan the interpolated value
				
				ScanPosition spos = interpolationPositon;
				ParseState state = this.getScanner().newInitialState();
				for( int i = 0 ; i < insideExpression.length(); i++){
					char s = insideExpression.charAt(i);
					spos.incrementColumn();
					state = state.receive(pos, s, tokensQueue);
				
					if (s == '\n'){
						spos.incrementLine();
					}
				}
				// force stop on the current state.
				state.receive(pos, (char)0, tokensQueue); // 0 is a stop char
				// send ) operator
				tokensQueue.accept(grammar.maybeMatch(pos, ")").get());
				
				// send . operator
				tokensQueue.accept(grammar.maybeMatch(pos, ".").get());
				
				// send toString 
				tokensQueue.accept(grammar.maybeMatch(pos, "asString").get());
				
				// send ( operator 
				tokensQueue.accept(grammar.maybeMatch(pos, "(").get());
				
				// send ) operator 
				tokensQueue.accept(grammar.maybeMatch(pos, ")").get());
				
				// send ++ operator
				tokensQueue.accept(grammar.maybeMatch(pos, "++").get());
	                
	                
			
				
			}
		} else {
			if (escape){
				escape = false;
			}
			if (readInterpolation){
				interpolated.append(c);
			} else {
				builder.append(c);
			}
			
		}
		return this;
	}



}
