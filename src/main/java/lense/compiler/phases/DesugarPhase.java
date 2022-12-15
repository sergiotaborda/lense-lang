package lense.compiler.phases;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;

public class DesugarPhase implements CompilerPhase{
	
	private CompilerListener listener;
	private String innerPropertyPrefix;
	
	public DesugarPhase(CompilerListener listener){
		this.listener = listener;
	}
	
	
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
				TreeTransverser.transverse(ct,new DesugarVisitor(ct.getSemanticContext(), innerPropertyPrefix));
			} catch (CompilationError e){
				listener.error(new CompilerMessage(e));
				return new CompilationResult(e);
			}
		}
		return result;
	}


    public String getInnerPropertyPrefix() {
        return innerPropertyPrefix;
    }


    public void setInnerPropertyPrefix(String innerPropertyPrefix) {
        this.innerPropertyPrefix = innerPropertyPrefix;
    }

}
