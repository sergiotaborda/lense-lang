package lense.compiler.graph;

import lense.compiler.graph.Graph.Vertex;

public class FollowVertexTraversalEvent<V, E> extends VertexTraversalEvent<V, E>{

	private final boolean firstcross;

	public FollowVertexTraversalEvent(Vertex<V, E> vertex, boolean firstcross) {
		super(vertex);
		
		this.firstcross = firstcross;
	}

	public boolean isFirstcross() {
		return firstcross;
	}

}
