package lense.compiler.crosscompile.java;

import java.io.File;
import java.util.Map;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.repository.UpdatableTypeRepository;

public final class JavalizePhase implements CompilerPhase {

	
	private CompilerListener listener;
	private Map<String, File> nativeTypes;
    private UpdatableTypeRepository typeContainer;
	
	public JavalizePhase (CompilerListener listener, Map<String, File> nativeTypes, UpdatableTypeRepository typeContainer){
		this.listener = listener;
		this.nativeTypes = nativeTypes;
		this.typeContainer = typeContainer;
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
				// cannot share semantic context among classes
				try {
					TreeTransverser.transverse(ct,new JavalizeVisitor(ct.getSemanticContext(), this.nativeTypes, typeContainer));
				} catch (CompilationError e){
					listener.error(new CompilerMessage(e.getMessage()));
					return new CompilationResult(e);
				}
			}
		}
		return result;
	}

}
