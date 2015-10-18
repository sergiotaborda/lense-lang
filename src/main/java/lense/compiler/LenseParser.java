/**
 * 
 */
package lense.compiler;

import compiler.lexer.TokenStream;
import compiler.parser.BottomUpParser;
import compiler.parser.nodes.ParserTreeNode;
import lense.compiler.LenseAwareTokenStream;
import lense.compiler.LenseLanguage;

/**
 * 
 */
public class LenseParser extends BottomUpParser {

	/**
	 * Constructor.
	 * @param senseLanguage
	 */
	public LenseParser(LenseLanguage senseLanguage) {
		super(senseLanguage);
	}
	
	public ParserTreeNode parse(TokenStream tokens) {
		return super.parse(new LenseAwareTokenStream(tokens));
	}

}
