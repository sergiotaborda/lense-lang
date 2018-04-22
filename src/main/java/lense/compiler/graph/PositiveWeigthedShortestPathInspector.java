package lense.compiler.graph;

import java.util.Collection;
import java.util.PriorityQueue;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.VertextInfoManager.VertexInfo;
import lense.compiler.graph.VertextInfoManager.VertexInfoVisitor;

/**
 * Implements Dijkstra's algorithm to termine the shortest path between two vertices on a graph.
 */
public class PositiveWeigthedShortestPathInspector implements ShortestPathInspector{

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

	private static class Path implements Comparable<Path> {

		private Vertex targetVertex;
		private double cost;
		
		public Path( Vertex<?, ?> targetVertex, double cost) {
			this.targetVertex = targetVertex;
			this.cost = cost;
		}

		@Override
		public int compareTo(Path other) {
			return Double.compare(this.cost, other.cost);
		}
		
		public boolean equals (Object other){
			return other instanceof Path && equalsOther((Path) other);
		}
		
		/**
		 * @param other
		 * @return
		 */
		private boolean equalsOther(Path other) {
			return this.cost == other.cost && this.targetVertex.equals(other.targetVertex);
		}

		public int hashCode(){
			return targetVertex.hashCode();
		}
		
		
	}
	
	public PositiveWeigthedShortestPathInspector (){}
	
	@Override
	public <E, V> GraphPath<E, V> getPath(Graph<E, V> graph, V startVertex, V endVertex) {
		
		final QueueGraphPath<E, V> result = new QueueGraphPath<E, V>(graph);
		
		VertextInfoManager manager = new VertextInfoManager();
		
		Collection<Vertex<V, E>> all = graph.getVertices();


		if (!all.isEmpty()){

			Vertex<V, E> start = graph.getVertex(startVertex);

			PriorityQueue<Path> pq = new PriorityQueue<Path>();

			pq.add(new Path(start,0));
			manager.info(start).dist =0;
			
			int nodesSeen = 0;
			int vertexCount = all.size();
			while (!pq.isEmpty() && nodesSeen < vertexCount){
				
				Path vrec = pq.remove();
				Vertex<V, E> v = vrec.targetVertex;
				
				VertexInfo infoV = manager.info(v);
				if (!infoV.hasZeroScratch()){
					continue; // already processed
				}
				
				infoV.setScratch(1);
				nodesSeen++;
				
				for (Edge<V, E> e : v.getOutjacentEdges() ){
					Vertex<V, E> w = e.getTargetVertex();
					double cvw = e.getCost();
					
					if (cvw < 0){
						throw new IllegalStateException("This graph has negative edge costs");
					}
					
					VertexInfo infoW = manager.info(w);
					
					if (Double.compare(infoW.dist, infoV.dist + cvw) > 0 ) {
						
						infoW.dist = infoV.dist + cvw;
						infoW.prev = v;
						infoW.connectingEdge = e;
						pq.add(new Path(w, infoW.dist));
					}
				}
			}
	
			manager.doVisit(new ResultVertexVisitor<E, V>(result) , graph.getVertex(endVertex));
		}	
		
		return result;
	}

}
