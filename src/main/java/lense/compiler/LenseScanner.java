/**
 * 
 */
package lense.compiler;

import compiler.Grammar;
import compiler.lexer.ParseState;
import compiler.lexer.Scanner;
import compiler.lexer.StringLiteralTokenState;
import compiler.lexer.TokenState;
import lense.compiler.LenseStringLiteralTokenState;

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
}
