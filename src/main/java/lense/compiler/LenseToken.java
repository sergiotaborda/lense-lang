/**
 * 
 */
package lense.compiler;

import compiler.SymbolBasedToken;
import compiler.TokenSymbol;
import compiler.lexer.ScanPosition;

/**
 * 
 */
public class LenseToken extends SymbolBasedToken {

	/**
	 * Constructor.
	 * @param position
	 * @param text
	 * @param symbol
	 */
	public LenseToken(ScanPosition position, String text, TokenSymbol symbol) {
		super(position, text, symbol);
	}

}
