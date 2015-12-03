/**
 * 
 */
package lense.compiler.graph;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;

/**
 * 
 */
public interface GraphFilter<V, E> {

	public boolean accepts (Vertex<V, E> vertex);
	public boolean accepts (Edge<V, E> edge);

}
