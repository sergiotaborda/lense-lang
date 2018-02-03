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

/**
 * 
 */
public class SemanticAnalysisPhase implements CompilerPhase {


	private CompilerListener listener;

	public SemanticAnalysisPhase(CompilerListener listener){
		this.listener = listener;
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
			try {
				TreeTransverser.transverse(ct,new SemanticVisitor(ct.getSemanticContext()));
				TreeTransverser.transverse(ct,new EnsureNotFundamentalTypesVisitor(ct.getSemanticContext()));
			} catch (CompilationError e){
				listener.error(new CompilerMessage(e.getMessage()));
				return new CompilationResult(e);
			}
		}
		return result;
	}
	

}
