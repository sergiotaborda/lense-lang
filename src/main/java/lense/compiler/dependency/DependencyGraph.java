package lense.compiler.dependency;

import java.util.Optional;

import lense.compiler.graph.DirectGraph;

public class DependencyGraph<D extends Dependency> extends DirectGraph<DependencyRelation,D>{

	public Optional<D> findDependencyNode(String name){
		return getVertices().stream().filter(v -> v.getObject().getDependencyIdentifier().equals(name)).map(v -> v.getObject()).findAny();
	}
}
