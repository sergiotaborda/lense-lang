/**
 * 
 */
package lense.compiler.graph;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;

/**
 * 
 */
public interface GraphVisitor<E,V> {

	public void onBeginGraph(Graph<E, V> g);
	
	public void onEndGraph(Graph<E, V> g);
	
	public void onBeginVertex(Vertex<V, E> vertex);
	
	public void onEndVertex(Vertex<V, E> vertex);
	
	public void onEdge(Edge<V, E> edge);

}
