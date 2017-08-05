package lense.compiler.ir;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.trees.TreeTransverser;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.ir.tac.TacInstructionsVisitor;

public class OutToIR implements CompilerBackEnd {

	@Override
	public void use(CompiledUnit unit) {

		for (ClassTypeNode ct : ((UnitTypes)unit.getAstRootNode()).getTypes()){
			TreeTransverser.transverse(unit.getAstRootNode(), new TacInstructionsVisitor(ct.getSemanticContext(), ct.getTypeDefinition()));
		}
		
	}

}
