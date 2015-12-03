/**
 * 
 */
package lense.compiler.crosscompile.java;

import compiler.Language;

/**
 * 
 */
public class JavaLanguage extends Language{

	/**
	 * Constructor.
	 * @param grammar
	 */
	public JavaLanguage() {
		super(new JavaGrammar());
	}

}
