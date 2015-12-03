/**
 * 
 */
package lense.compiler.dependency;

import java.util.Optional;

import lense.compiler.graph.DirectGraph;

/**
 * 
 */
public class DependencyGraph extends DirectGraph<DependencyRelation,DependencyNode>{

	
	public Optional<DependencyNode> findDependencyNode(String name){
		return getVertices().stream().filter(v -> v.getObject().getName().equals(name)).map(v -> v.getObject()).findAny();
	}
}
