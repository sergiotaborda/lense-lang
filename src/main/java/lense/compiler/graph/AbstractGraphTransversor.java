package lense.compiler.graph;

import java.util.ArrayList;
import java.util.List;



/**
 * 
 */
public abstract class AbstractGraphTransversor<E,V> implements GraphTransversor<E,V> {

	//private final EventListenersSet<GraphTranverseListener> listenerSet = EventListenersSet.newSet(GraphTranverseListener.class);
	
	List<GraphTranverseListener<V,E>> listenerSet = new ArrayList<>();
	
	@Override
	public void  addListener(GraphTranverseListener<V,E>  listener) {
		listenerSet.add(listener);
	}

	@Override
	public  void removeListener(GraphTranverseListener<V,E>  listener) {
		listenerSet.remove(listener);
	}

	protected final  GraphTranverseListener<V,E> getListenerSet(){
		if (listenerSet.size() == 1){
			return listenerSet.get(0);
		}
		
		return new GraphTranverseListener<V,E>(){

			@Override
			public void beginEdgeTraversed(EdgeTraversalEvent<E,V> e) {
				for (GraphTranverseListener lt : listenerSet ){
					lt.beginEdgeTraversed(e);
				}
			}

			@Override
			public void endEdgeTraversed(EdgeTraversalEvent<E,V> e) {
				for (GraphTranverseListener lt : listenerSet ){
					lt.endEdgeTraversed(e);
				}
			}

			@Override
			public void endVertex(VertexTraversalEvent<V, E> e) {
				for (GraphTranverseListener lt : listenerSet ){
					lt.endVertex(e);
				}
			}

			@Override
			public void beginVertex(VertexTraversalEvent<V, E> e) {
				for (GraphTranverseListener lt : listenerSet ){
					lt.beginVertex(e);
				}
			}

		
		};
	}
	
	
}
