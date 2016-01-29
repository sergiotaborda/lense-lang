package lense.compiler.ir;

import lense.compiler.ir.java.TacInstructionsVisitor;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.trees.TreeTransverser;

public class OutToIR implements CompilerBackEnd {

	@Override
	public void use(CompiledUnit unit) {

		TreeTransverser.tranverse(unit.getAstRootNode(), new TacInstructionsVisitor());
	}

}
