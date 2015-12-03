package lense.compiler.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.VertextInfoManager.VertexInfo;
import lense.compiler.graph.VertextInfoManager.VertexInfoVisitor;

/**
 * Implements an algorithm to determine the shortest path between two vertices on a non weigthed graph.
 */
public class UnWeigthedShortestPathInspector implements ShortestPathInspector{

	/**
	 * @param <E>
	 * @param <V>
	 */
	private static final class ResultVertexVisitor<E, V> implements
			VertexInfoVisitor<E, V> {
		/**
		 * 
		 */
		private final QueueGraphPath<E, V> result;

		/**
		 * Constructor.
		 * @param result
		 */
		private ResultVertexVisitor(QueueGraphPath<E, V> result) {
			this.result = result;
		}

		@Override
		public void beginVisitVertex(Vertex<V, E> vertex) {}

		@Override
		public void endVisitVertex(Vertex<V, E> vertex) {}

		@Override
		public void visitEdge(Edge<V, E> connectingEdge) {
			result.addFirstEdge(connectingEdge);
		}
	}

	public UnWeigthedShortestPathInspector (){}
	
	@Override
	public <E, V> GraphPath<E, V> getPath(Graph<E, V> graph, V startVertex, V endVertex) {
		
		final QueueGraphPath<E, V> result = new QueueGraphPath<E, V>(graph);
		
		VertextInfoManager manager = new VertextInfoManager();
		
		Collection<Vertex<V, E>> all = graph.getVertices();


		if (!all.isEmpty()){

			Vertex<V, E> start = graph.getVertex(startVertex);
			
			Queue<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();

			q.add(start);
			manager.info(start).dist = 0;

			while ( !q.isEmpty()){
				Vertex<V, E> v = q.remove();

				VertexInfo infoV = manager.info(v);
				
				for (Edge<V, E> e : v.getOutjacentEdges()){

					Vertex<V, E> w = e.getTargetVertex();

					VertexInfo infoW = manager.info(w);

					if (Double.compare(infoW.dist, Double.MAX_VALUE) == 0 ) {

						infoW.dist = infoV.dist + 1;
						infoW.prev = v;
						infoW.connectingEdge = e;
						q.add(w);
					}
				}

			}
	
			manager.doVisit(new ResultVertexVisitor<E, V>(result) , graph.getVertex(endVertex));
		}	
		
		return result;
	}

}
