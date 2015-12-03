package lense.compiler.graph;

public interface GraphTranverseListener<V, E> {

	
	void beginEdgeTraversed(EdgeTraversalEvent<E,V> e);
	
	void endEdgeTraversed(EdgeTraversalEvent<E,V> e);
	
	void endVertex(VertexTraversalEvent<V, E> e);
	   
	void beginVertex(VertexTraversalEvent<V, E> e);
	   
}
