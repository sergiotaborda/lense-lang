package lense.compiler.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;
import lense.compiler.graph.VertextInfoManager.VertexInfo;

/**
 * Transverses a graph in topologic order, meaning from the vertex with less
 * edges to the one with more edges.
 * 
 */
public class TopologicOrderTransversor<E, V> extends AbstractGraphTransversor<E, V> {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @param graph
	 *            the graph to transverse.
	 * @param startVertex
	 *            not used. you can pass <code>null</code>.
	 */
	@Override
	public void transverse(Graph<E, V> graph, V startVertex) {
		Collection<Vertex<V, E>> all = graph.getVertices();

		VertextInfoManager manager = new VertextInfoManager();

		if (!all.isEmpty()) {

			LinkedList<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();

			// compute ingree

			for (Vertex<V, E> v : all) {
				for (Edge<V, E> e : v.getOutjacentEdges()) {
					manager.info(e.getTargetVertex()).incrementScratch();
				}
			}

			// enqueue those with ingree zero
			for (Vertex<V, E> v : all) {
				if (manager.info(v).hasZeroScratch()) {
					q.add(v);
				}
			}

			Set<Vertex<V, E>> visited = new HashSet<>();

			while (!q.isEmpty()) {

				GraphTranverseListener<V, E> broadcastEvent = this.getListenerSet();

				int iterations;
				for (iterations = 0; !q.isEmpty(); iterations++) {

					Vertex<V, E> v = q.removeFirst();

					visited.add(v);

					broadcastEvent.beginVertex(new VertexTraversalEvent<V, E>(v));

					for (Edge<V, E> e : v.getOutjacentEdges()) {

						broadcastEvent.beginEdgeTraversed(new EdgeTraversalEvent<E, V>(e));

						Vertex<V, E> w = e.getTargetVertex();
						double cvw = e.getCost();

						VertexInfo infoW = manager.info(w);

						infoW.decrementScratch();
						if (infoW.hasZeroScratch()) {
							q.add(w);
						}

						VertexInfo infoV = manager.info(v);

						broadcastEvent.endEdgeTraversed(new EdgeTraversalEvent<E, V>(e));

						if (Double.compare(infoV.dist, Double.MAX_VALUE) == 0) {
							continue;
						}
						if (Double.compare(infoW.dist, infoV.dist + cvw) > 0) {
							infoW.dist = infoV.dist + cvw;
							infoW.prev = v;
							infoW.connectingEdge = e;
						}
					}

					broadcastEvent.endVertex(new VertexTraversalEvent<V, E>(v));
				}

				if (iterations < all.size()) {
					Collection<Vertex<V, E>> remaining = new LinkedList<>(all);
					remaining.removeAll(visited);
					// graph contains more than one graph
					
					int min = Integer.MAX_VALUE;
					for (Vertex<V, E> v : remaining) {
						int scratch = manager.info(v).getScratch();
						
						min = Math.min(min,  scratch);
						if (scratch == 0) {
							q.add(v);
						}
					}
					
					if (q.isEmpty() && !remaining.isEmpty()) {
						if (min == 1) {
							for (Vertex<V, E> v : remaining) {
								if ( manager.info(v).getScratch() == min) {
									q.add(v);
								}
							}
						} else {
							throw new CycleFoundException();
						}
					}
					
					
				} else if (iterations > all.size()) {
					throw new CycleFoundException();
				}
			}

		}

	}

}
