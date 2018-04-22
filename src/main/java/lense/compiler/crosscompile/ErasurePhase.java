package lense.compiler.crosscompile;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;

public final class ErasurePhase implements CompilerPhase{
   
    private CompilerListener listener;

    public ErasurePhase (CompilerListener listener){
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
        	if (!ct.isNative()) {
                try {
                    TreeTransverser.transverse(ct,new BoxingPointClassificationVisitor());
                    TreeTransverser.transverse(ct,new BoxingPointErasureVisitor());
                } catch (CompilationError e){
                    listener.error(new CompilerMessage(e.getMessage()));
                    return new CompilationResult(e);
                }
        	}
        }
        return result;
    }

}
