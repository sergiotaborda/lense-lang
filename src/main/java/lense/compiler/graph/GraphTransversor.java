package lense.compiler.graph;


/**
 * 
 */
public interface GraphTransversor<E,V> {

	/**
	 * Transversed the graph in the order given by this transversor.
	 * @param <E> The edge object type
	 * @param <V> The vertex object type
	 * @param graph the graph to transversed
	 * @param startVertex the start vertex.
	 * 
	 */
	public  void transverse(Graph<E, V> graph, V startVertex);
	
	
	public void addListener(GraphTranverseListener<V,E >  listener);
	
	public  void removeListener(GraphTranverseListener<V,E> listener);
}
