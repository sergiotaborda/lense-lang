package lense.compiler.graph;

public interface ShortestPathInspector {

	/**
	 * 
	 * @param <E>
	 * @param <V>
	 * @param graph
	 * @param startVertex
	 * @param endVertex
	 * @return
	 */
	public <E, V> GraphPath<E, V> getPath(Graph<E,V> graph, V startVertex, V endVertex);
}
