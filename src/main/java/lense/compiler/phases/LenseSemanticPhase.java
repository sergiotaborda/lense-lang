/**
 * 
 */
package lense.compiler.phases;

import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import compiler.CompilationResult;
import compiler.CompiledUnit;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;

/**
 * 
 */
public class LenseSemanticPhase implements CompilerPhase {


	private CompilerListener listener;

	public LenseSemanticPhase(CompilerListener listener){
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
		for (ClassTypeNode ct : ((UnitTypes)result.getCompiledUnit().getAstRootNode()).getTypes()){
			// cannot share semantic context among classes
			try {
				TreeTransverser.transverse(ct,new SemanticVisitor(ct.getSemanticContext()));
			} catch (CompilationError e){
				listener.error(new CompilerMessage(e.getMessage()));
				return new CompilationResult(e);
			}
		}
		return result;
	}
	
	/**
	 * @param t
	 */
	public void analise(UnitTypes t) {

		
	}

}
