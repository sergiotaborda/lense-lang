package lense.compiler.graph;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;

import lense.compiler.graph.Graph.Vertex;

public class FollowTransversor<E, V> extends AbstractGraphTransversor<E, V> {

	@Override
	public void transverse(Graph<E, V> graph, V start) {

		Collection<Vertex<V, E>> all = new HashSet<>(graph.getVertices());

		VertextInfoManager manager = new VertextInfoManager();

		if( all.isEmpty()) {
			return;
		}
		
		Vertex<V, E> startVertex;
		
		
		while(!all.isEmpty()) {
			startVertex = all.iterator().next();
		
			Deque<VertexVisit<V, E>> deque = new LinkedList<>();
			
			addVertex(startVertex, deque, manager, all);
			
			var listeners = this.getListenerSet();
			while(!deque.isEmpty()) {
				var visit = deque.pop();
				
				var event = new FollowVertexTraversalEvent<>(visit.vertex, !visit.proxy);
				listeners.beginVertex(event);
				listeners.endVertex(event);
			}
		}
		
		
	}
	
	private static <V,E> void addVertex(Vertex<V, E> currentVertex, Deque<VertexVisit<V, E>> deque ,VertextInfoManager manager , Collection<Vertex<V, E>> all) {

		var info = manager.info(currentVertex);
		
		if(info.visited == 0) {
			deque.addFirst(new VertexVisit<V, E>(currentVertex, false) );
			manager.info(currentVertex).visited = 1;
			all.remove(currentVertex);
			
			for(var edge : currentVertex.getOutjacentEdges()) {
				addVertex(edge.getTargetVertex(), deque, manager, all);
			}
		} else {
			deque.addFirst(new VertexVisit<V, E>(currentVertex, true)); // proxy
		}
	
	}
	

}

class VertexVisit<V, E>{

	public Vertex<V, E> vertex;
	public boolean proxy;

	public VertexVisit(Vertex<V, E> vertex, boolean proxy) {
		this.vertex = vertex;
		this.proxy = proxy;
	}
	
	public String toString() {
		return vertex.toString() + (proxy ? "'" : "");
	}
}


