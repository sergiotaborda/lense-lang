/**
 * 
 */
package lense.compiler.dependency;

import java.util.Optional;

import lense.compiler.graph.DirectGraph;

/**
 * 
 */
public class ModuleDependencyGraph extends DirectGraph<DependencyRelation,ModuleDependencyNode>{

	
	public Optional<ModuleDependencyNode> findDependencyNode(String name){
		return getVertices().stream().filter(v -> v.getObject().getName().equals(name)).map(v -> v.getObject()).findAny();
	}
}
