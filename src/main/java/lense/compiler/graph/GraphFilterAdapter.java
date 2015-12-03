/**
 * 
 */
package lense.compiler.graph;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;

/**
 * 
 */
public class GraphFilterAdapter<V, E> implements  GraphFilter< V, E>{

	public boolean accepts (Vertex<V, E> vertex){
		return true;
	}
	
	public boolean accepts (Edge<V, E> edge){
		return true;
	}

}
