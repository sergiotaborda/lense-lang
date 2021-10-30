package lense.compiler.phases;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import compiler.CompilationResult;
import compiler.CompilerPhase;
import lense.compiler.CompilationError;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;

public class TypeClassInterpolationPhase implements CompilerPhase {

	private Map<String, ClassTypeNode> mapping = new HashMap<>();
	
	public TypeClassInterpolationPhase(Collection<ClassTypeNode> allTypes) {
		for (var m : allTypes) {
			mapping.put(m.getFullname(), m);
		}
	}

	@Override
	public CompilationResult apply(CompilationResult result) {
		if (result.isError()){
			return result;
		}
		try {
			UnitTypes t = (UnitTypes) result.getCompiledUnit().getAstRootNode();

	
			for (ClassTypeNode ct :  t.getTypes()){
				var pos = ct.getFullname().indexOf("$$");
				
				
				if (pos >=0) {
					var body = ct.getChildren(ClassBodyNode.class).get(0);
					var name = ct.getFullname().substring(0,pos);
					var instanceClass = mapping.get(name);
					
					if (instanceClass != null) {
						if (instanceClass.getSatisfiedTypeClasses() != null) {
							
							var methods = instanceClass.getChildren(ClassBodyNode.class).get(0).getChildren(MethodDeclarationNode.class);
							
							for (var m : methods) {
								if (m.isSatisfy()) { // TODO clone
									m.setProperty("moved", true);
									m.setSatisfy(false);
									
									var type = m.getSuperMethod().getReturningType();
									
									if (type instanceof DeclaringTypeBoundedTypeVariable d) {
										type = d.getDeclaringType();
									}
									
									m.getReturnType().setTypeParameter(type);
									m.getReturnType().setTypeVariable(type);
									body.add(m);
								}
							}
						}
					}
					
				}
				
			}
				
			
			return result;
		} catch (CompilationError e){
			return new CompilationResult(e);
		}
		
	

	}

}
