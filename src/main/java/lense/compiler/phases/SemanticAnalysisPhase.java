/**
 * 
 */
package lense.compiler.phases;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;

/**
 * 
 */
public class SemanticAnalysisPhase implements CompilerPhase {


	private CompilerListener listener;
	private UpdatableTypeRepository typeRepository;

	public SemanticAnalysisPhase(UpdatableTypeRepository typeRepository, CompilerListener listener){
		this.listener = listener;
		this.typeRepository = typeRepository;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CompilationResult apply(CompilationResult result) {
	
		if (result.isError()){
			return result;
		}
		
		UnitTypes types = result.getCompiledUnit() == null ? null : (UnitTypes)result.getCompiledUnit().getAstRootNode();
		
		if (types == null){
			return new CompilationResult(new RuntimeException("Unexpected Error. Result as no node."));
		}
		for (ClassTypeNode ct : types.getTypes()){
			// cannot share semantic context among classes
			if (!ct.isNative()) {
				
				// attach the repository with loaded types
				SemanticContext ctx = ct.getSemanticContext().withRepository(typeRepository);
				
				try {
					TreeTransverser.transverse(ct,new SemanticVisitor(ctx));
					TreeTransverser.transverse(ct,new EnsureNotFundamentalTypesVisitor(ctx));
				} catch (CompilationError e){
					listener.error(new CompilerMessage(e.getMessage()));
					return new CompilationResult(e);
				}
			} 
		}
		return result;
	}
	

}
