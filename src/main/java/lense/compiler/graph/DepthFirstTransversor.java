package lense.compiler.graph;

import java.util.Deque;

import lense.compiler.graph.Graph.Vertex;


public class DepthFirstTransversor<E,V> extends  AbstractGraphFirstTransversor<E,V> {


	@Override
	protected Vertex<V, E> nextVertex(Deque<Vertex<V, E>> q) {
		return q.removeLast(); // uses a stack
	}


	

}
