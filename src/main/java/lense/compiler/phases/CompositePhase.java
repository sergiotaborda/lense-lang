package lense.compiler.phases;

import java.util.LinkedList;
import java.util.List;

import compiler.CompilationResult;
import compiler.CompilerPhase;

public class CompositePhase implements CompilerPhase{

	
	List<CompilerPhase> phases = new LinkedList<>();
	
	
	public CompositePhase add(CompilerPhase a){
		phases.add(a);
		return this;
	}
	
	@Override
	public CompilationResult apply(CompilationResult unit) {
		CompilationResult r = unit;
		for(CompilerPhase c : phases){
			r = c.apply(r);
		}
		return r;
	}

}
