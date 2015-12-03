/**
 * 
 */
package lense.compiler;

import lense.compiler.phases.LenseSemanticPhase;
import lense.compiler.phases.NameResolutionPhase;
import compiler.AstCompiler;
import compiler.CompilationResultSet;
import compiler.CompilationUnit;
import compiler.CompilationUnitSet;
/**
 * 
 */
public class LenseSourceCompiler extends AstCompiler {

	/**
	 * Constructor.
	 * @param language
	 */
	public LenseSourceCompiler() {
		super(new LenseLanguage());
	}

	public CompilationResultSet parse(CompilationUnitSet unitSet){
		LenseTypeRepository repo = new LenseTypeRepository();
		
		PackageResolver resolver = new PackageResolver(){

			@Override
			public String resolveUnitPackageName(CompilationUnit compilationUnit) {
				return "noname";
			}
			
		};
		return super.parse(unitSet).passBy(new NameResolutionPhase(repo, resolver, this.getListener())).passBy(new LenseSemanticPhase(this.getListener()));
	}
}
