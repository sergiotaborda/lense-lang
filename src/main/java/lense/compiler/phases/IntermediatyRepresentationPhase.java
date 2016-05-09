package lense.compiler.phases;

import compiler.CompilationResult;
import compiler.CompilerPhase;
import compiler.trees.TreeTransverser;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.ir.tac.TacInstructionsVisitor;

public class IntermediatyRepresentationPhase implements CompilerPhase{

	@Override
	public CompilationResult apply(CompilationResult result) {
		
		if (result.isError()){
			return result;
		}
		for (ClassTypeNode ct : ((UnitTypes)result.getCompiledUnit().getAstRootNode()).getTypes()){
			TreeTransverser.transverse(result.getCompiledUnit().getAstRootNode(), new TacInstructionsVisitor(ct.getSemanticContext(), ct.getTypeDefinition()));
		}
		return result;
	}

}
