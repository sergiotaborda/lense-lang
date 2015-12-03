package lense.compiler.graph;

import lense.compiler.graph.Graph.Edge;


public class EdgeTraversalEvent<E, V> {

	private Edge<V,E> edge;

	public EdgeTraversalEvent(Edge<V,E> edge) {
		this.edge = edge;
	}
	
	public Edge<V,E> getEdge(){
		return edge;
	}

}
