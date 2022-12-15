package lense.compiler.crosscompile;

import java.util.Map;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.filesystem.SourceFile;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;

public class NativePeersPhase implements CompilerPhase{
   
    private CompilerListener listener;
	private Map<String, SourceFile> nativeTypes;

    public NativePeersPhase (CompilerListener listener, Map<String, SourceFile> nativeTypes){
        this.listener = listener;
        this.nativeTypes = nativeTypes;
    }
    
    @Override
    public CompilationResult apply(CompilationResult result) {
        if (result.isError()){
            return result;
        }
        
        UnitTypes types = result.getCompiledUnit() == null ? null : (UnitTypes)result.getCompiledUnit().getAstRootNode();
        
        if (types == null){
            return new CompilationResult(new RuntimeException("Unexpected Error. Result has no node."));
        }
        for (ClassTypeNode ct : types.getTypes()){
        	if (!ct.isNative()) {
        	    
        	    SemanticContext ctx = ct.getSemanticContext();

                try {
                    TreeTransverser.transverse(ct,new NativePeerVisitor(nativeTypes));
                    
                } catch (CompilationError e){
                    listener.error(new CompilerMessage(e));
                    return new CompilationResult(e);
                }
        	}
        }
        return result;
    }

}
