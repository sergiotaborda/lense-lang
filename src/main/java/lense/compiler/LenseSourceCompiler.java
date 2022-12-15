/**
 * 
 */
package lense.compiler;

import compiler.AstCompiler;
import compiler.CompilationResultSet;
import compiler.CompilationUnit;
import compiler.CompilationUnitSet;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.phases.SemanticAnalysisPhase;
import lense.compiler.typesystem.PackageResolver;
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
		FundamentalTypesModuleContents repo = new FundamentalTypesModuleContents(
			new EditableModuleDescriptor("lense.core", null)		
		);
		
		PackageResolver resolver = new PackageResolver(){

			@Override
			public String resolveUnitPackageName(CompilationUnit compilationUnit) {
				return "noname";
			}
			
		};
		return super.parse(unitSet)
				.passBy(new NameResolutionPhase(repo, resolver, this.getListener()))
				.passBy(new SemanticAnalysisPhase(repo,this.getListener()))
				//.passBy(new IntermediatyRepresentationPhase())
				;
	}
}
