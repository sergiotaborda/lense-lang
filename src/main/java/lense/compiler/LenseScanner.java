/**
 * 
 */
package lense.compiler;

import java.util.Optional;

import compiler.Grammar;
import compiler.lexer.ParseState;
import compiler.lexer.Scanner;
import compiler.lexer.Token;
import compiler.lexer.TokenState;
import compiler.lexer.VersionLiteralTokenState;

/**
 * 
 */
public class LenseScanner extends Scanner {

	/**
	 * Constructor.
	 * @param grammar
	 */
	public LenseScanner(Grammar grammar) {
		super(grammar);
	}

	/**
	 * @param tokenState
	 * @return
	 */
	public ParseState getStringLiteralTokenState(TokenState tokenState) {
		return new LenseStringLiteralTokenState(tokenState);
	}
	
	/**
	 * @param numberLiteralTokenState
	 * @return
	 */
	public ParseState getVersionLiteralTokenState(TokenState tokenState) {
		return new VersionLiteralTokenState(tokenState);
	}
	
	/**
	 * @param tokenState
	 * @return
	 */
	public ParseState getNumberLiteralTokenState(TokenState tokenState) {
		return new LenseNumberLiteralTokenState(tokenState);
	}
	
	public Optional<ParseState> matchToken(Token token, TokenState state) {
		if (token instanceof VersionLiteralToken){
			return Optional.of(this.getVersionLiteralTokenState(state));
		} else {
			return super.matchToken(token, state);
		}
	}
}
