/**
 * 
 */
package lense.compiler;

import compiler.Language;
import compiler.parser.LookupTable;
import compiler.parser.Parser;

/**
 * 
 */
public class LenseLanguage extends Language{

	/**
	 * Constructor.
	 * @param grammar
	 */
	public LenseLanguage() {
		super(new LenseGrammar());
	}
	
	public LookupTable getLookupTable() {
		return new LenseLookupTable((LenseGrammar)this.getGrammar());
	}
	
	public Parser parser() {
		return new LenseParser(this);
	}


}
