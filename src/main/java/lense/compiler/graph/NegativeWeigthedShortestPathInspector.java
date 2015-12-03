package lense.compiler.graph;

import java.util.Collection;
import java.util.LinkedList;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.VertextInfoManager.VertexInfo;
import lense.compiler.graph.VertextInfoManager.VertexInfoVisitor;

/**
 * Implements Bellman-Ford algorithm to determine the shortest path between two vertices on a graph with negative edge costs, but no cycles.
 */
public class NegativeWeigthedShortestPathInspector implements ShortestPathInspector{

	public NegativeWeigthedShortestPathInspector (){}
	
	@Override
	public <E, V> GraphPath<E, V> getPath(Graph<E, V> graph, V startVertex, V endVertex) {
		
		final QueueGraphPath<E, V> result = new QueueGraphPath<E, V>(graph);
		
		VertextInfoManager manager = new VertextInfoManager();
		
		Collection<Vertex<V, E>> all = graph.getVertices();


		if (!all.isEmpty()){

			Vertex<V, E> start = graph.getVertex(startVertex);
			
			LinkedList<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();
			
			q.add(start);
			manager.info(start).dist =0;
			
			final int vertexCount = all.size();
			
			while (!q.isEmpty() ){
				
				Vertex<V, E> v = q.removeFirst();
				
			
				VertexInfo infoV = manager.info(v);
				if (infoV.scratch++ > 2 * vertexCount){
					throw new IllegalStateException("Negative cycle detected");
				}
				

				for (Edge<V, E> e : v.getOutjacentEdges() ){
					Vertex<V, E> w = e.getTargetVertex();
					double cvw = e.getCost();
					
					VertexInfo infoW = manager.info(w);
					
					if (Double.compare(infoW.dist, infoV.dist + cvw) > 0 ) {
						
						infoW.dist = infoV.dist + cvw;
						infoW.prev = v;
						infoW.connectingEdge = e;
						// enqueue only if not enqued.
						if (infoW.scratch++ % 2 == 0){
							q.add(w);
						} else {
							infoW.scratch--;
						}
					}
				}
			}
	
			manager.doVisit(new VertexVisitor<E,V> (result) , graph.getVertex(endVertex));
		}	
		
		return result;
	}
	
	private static  class VertexVisitor<E,V> implements VertexInfoVisitor<E,V>{
		
		private final QueueGraphPath<E, V> result;

		public VertexVisitor(QueueGraphPath<E, V> result){
			this.result = result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void beginVisitVertex(Vertex<V, E> vertex) {
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void endVisitVertex(Vertex<V, E> vertex) {
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void visitEdge(Edge<V, E> connectingEdge) {
			result.addFirstEdge(connectingEdge);
		}
		
		
	}

}
