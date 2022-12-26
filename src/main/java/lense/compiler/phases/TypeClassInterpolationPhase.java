package lense.compiler.phases;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import compiler.CompilationResult;
import compiler.CompilerPhase;
import lense.compiler.CompilationError;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.*;
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

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

			var anyType = LenseTypeSystem.Any();
			
			for (ClassTypeNode ct :  t.getTypes()){
				var pos = ct.getFullname().indexOf("$$");
				
				
				if (pos >=0) {
					var body = ct.getChildren(ClassBodyNode.class).get(0);
					var name = ct.getFullname().substring(0,pos);
					var instanceClass = mapping.get(name);
					
				
					if (instanceClass != null && instanceClass.getSatisfiedTypeClasses() != null) {
						
						var methods = instanceClass.getChildren(ClassBodyNode.class).get(0).getChildren(MethodDeclarationNode.class);
						
						for (var m : methods) {
							if (m.isSatisfy()) { // TODO clone
								m.setProperty("moved", true);
								m.setSatisfy(false);
								
								var type = Optional.ofNullable(m.getSuperMethod()).map(it -> it.getReturningType())
										.orElse(m.getReturnType().getTypeVariable());
								
								if (type instanceof DeclaringTypeBoundedTypeVariable d) {
									type = d.getDeclaringType();
								}
								
					
								m.getReturnType().setTypeParameter(type);
								m.getReturnType().setTypeVariable(type);
								
				
								for (var f : m.getParameters().getChildren(FormalParameterNode.class)) {
									var previousName = f.getName();
									var newName = "$$" + previousName;
									f.setName(newName);
									
									var previousType = f.getTypeNode();
									f.setTypeNode(new TypeNode(anyType));
									
									m.getBlock().addFirst(new VariableDeclarationNode(previousName,
											previousType.getTypeVariable() , 
											new CastNode(new VariableReadNode(newName), previousType.getTypeVariable()))
									);
								}
								body.add(m);
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
