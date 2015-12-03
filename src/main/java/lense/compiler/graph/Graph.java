package lense.compiler.graph;

import java.util.Collection;
import java.util.List;



/**
 * Represents Graph of Edges of type E an Vertex of type V. 
 * An {@link Edge} is a (possible directional) relation between 2 {@link Vertex}.
 * 
 *   [V1] ---[E]--->[V2] 
 * 
 * @param <E>
 * @param <V>
 */
public interface Graph<E, V> {

	/**
	 * 
	 * A graph edge.
	 * An {@link Edge} is a (possible directional) relation between 2 {@link Vertex}.
	 * 
	 * @param <E> the type of object at the edge 
	 */
	public static interface Edge<V, E> {
		
		public E getObject();
		
		public Vertex<V, E> getTargetVertex();
		public Vertex<V, E> getSourceVertex();
		public double getCost();
		
	}
	
	/**
	 * A graph vertex. A vertex is a "point" in the graph.
	 * @param <V> the type of object at the vertex.
	 */
	public static interface Vertex<V, E> {
		
		public void reset();
		public V getObject();
		public List<Edge<V, E>> getOutjacentEdges();
		public List<Edge<V, E>> getIncidentEdges();
		
	}
	
	/**
	 * Add an edge to the Graph
	 * @param edgeObject
	 * @param sourceVertex
	 * @param targetVertex
	 * @param cost
	 */
	void addEdge( E edgeObject, V sourceVertex, V targetVertex, double cost);
	void removeVertex(Vertex<V,E> vertex);
	void removeEdge(Edge<V,E> edge);
	
	
	void filter(GraphFilter<V, E> filter);
	
	public Vertex<V,E> getVertex(V vertex);
	Collection<Vertex<V, E >> 	getVertices(); 
	
	Collection<Edge<V ,E>> 	getEdges();
	/**
	 * @param string
	 * @return
	 */
	boolean containsEdge(E edge); 

	public void visit(GraphVisitor<E, V> visitor);

	public List<Graph<E,V>> split();
}
