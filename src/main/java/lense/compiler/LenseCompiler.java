/**
 * 
 */
package lense.compiler;

import compiler.Compiler;
import lense.compiler.LenseLanguage;
import lense.compiler.LenseTypeResolver;
/**
 * 
 */
public class LenseCompiler extends Compiler {

	/**
	 * Constructor.
	 * @param language
	 */
	public LenseCompiler() {
		super(new LenseLanguage());
		
		addTypeResolver(LenseTypeResolver.getInstance());
	}

}
