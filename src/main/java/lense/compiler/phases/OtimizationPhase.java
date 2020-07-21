package lense.compiler.phases;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import compiler.CompilationResult;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.CompilerPhase;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilationError;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.SingleImportNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.context.SemanticContext;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.TypeSearchParameters;

public class OtimizationPhase implements CompilerPhase {


	private CompilerListener listener;

	public OtimizationPhase(CompilerListener listener){
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

		

		try {

			for (ClassTypeNode ct : types.getTypes()){

				// attach the repository with loaded types
//				SemanticContext ctx = ct.getSemanticContext();

				TreeTransverser.transverse(ct,new OtimizationVisitor());

			}

	
		} catch (CompilationError e){
			listener.error(new CompilerMessage(e.getMessage()));
			return new CompilationResult(e);
		}


		return result;
	}


}