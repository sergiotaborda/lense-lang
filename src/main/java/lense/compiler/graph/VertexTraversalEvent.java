package lense.compiler.graph;

import lense.compiler.graph.Graph.Vertex;

public class VertexTraversalEvent<V, E> {

	
	private Vertex<V,E> vertex;
	
	public VertexTraversalEvent (Vertex<V,E> vertex){
		this.vertex = vertex;
	}

	public Vertex<V,E> getVertex() {
		return vertex;
	}

}
