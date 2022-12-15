package lense.compiler.crosscompile.java;

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
import lense.compiler.repository.UpdatableTypeRepository;

public final class JavalizePhase implements CompilerPhase {

	protected static String AutoGenerateHashCodeAndEquals = "AutoGenerateHashCodeAndEquals";
	protected static String AutoGenerateAsString = "AutoGenerateAsString";
	
	private CompilerListener listener;
	private Map<String, SourceFile> nativeTypes;
    private UpdatableTypeRepository typeContainer;
	
	public JavalizePhase (CompilerListener listener, Map<String, SourceFile> nativeTypes, UpdatableTypeRepository typeContainer){
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
			
			try {
				TreeTransverser.transverse(ct,new NativeVerificationVisitor(ct.getSemanticContext(), this.nativeTypes, typeContainer));
				if (!ct.isNative()) {
					// cannot share semantic context among classes
					TreeTransverser.transverse(ct,new JavalizeVisitor(ct.getSemanticContext(), typeContainer));
				} 
			
			} catch (CompilationError e){
				listener.error(new CompilerMessage(e));
				return new CompilationResult(e);
			}
		}
		return result;
	}

}
