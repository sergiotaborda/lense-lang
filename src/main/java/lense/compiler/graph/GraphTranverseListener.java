package lense.compiler.graph;

public interface GraphTranverseListener<V, E> {

	
	default void beginEdgeTraversed(EdgeTraversalEvent<E,V> e) {};
	
	default void endEdgeTraversed(EdgeTraversalEvent<E,V> e) {};
	
	default void endVertex(VertexTraversalEvent<V, E> e) {};
	   
	default void beginVertex(VertexTraversalEvent<V, E> e) {};
	   
}
