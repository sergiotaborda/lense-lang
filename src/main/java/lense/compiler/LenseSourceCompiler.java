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
public class LenseSourceCompiler extends Compiler {

	/**
	 * Constructor.
	 * @param language
	 */
	public LenseSourceCompiler() {
		super(new LenseLanguage());
		
		addTypeResolver(LenseTypeResolver.getInstance());
	}

}
