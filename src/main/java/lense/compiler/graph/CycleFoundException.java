/**
 * 
 */
package lense.compiler.graph;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lense.compiler.graph.Graph.Vertex;

/**
 * 
 */
public class CycleFoundException extends GraphException {


	private static final long serialVersionUID = -2809107183322751179L;

	private static <E,V> String pathOf(List<Vertex<V, E>> vertexList) {
		return vertexList.stream().map(v -> v.getObject().toString()).collect(Collectors.joining("->"));
	}

	@SuppressWarnings("rawtypes")
	private final List vertexList;
	
	public <E,V> CycleFoundException (List<Vertex<V, E>> vertexList){
		super("Dependency graph as a cycle: " + pathOf(vertexList));
		
		this.vertexList = vertexList;
	}
	
	@SuppressWarnings("unchecked")
	public <E,V>  List<Vertex<V, E>> getVertexesInPath(){
		return Collections.unmodifiableList(vertexList);
	}
}
