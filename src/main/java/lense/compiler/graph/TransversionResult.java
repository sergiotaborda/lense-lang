package lense.compiler.graph;

import java.util.HashMap;
import java.util.Map;

import lense.compiler.graph.Graph.Vertex;

public class TransversionResult<E, V> {

	public class VertexInfo {

		protected int scratch = 0;
		protected double dist = Double.MAX_VALUE;
		protected Vertex<V, E> prev;

	}

	private final Map<Vertex<V, E>, VertexInfo> infos = new HashMap<Vertex<V, E>, VertexInfo>();
	private Graph<E, V> graph;

	public TransversionResult(Graph<E, V> graph) {
		this.graph = graph;
	}

	protected VertexInfo info(Vertex<V, E> vertex) {
		VertexInfo info = infos.get(vertex);
		if (info == null) {
			info = new VertexInfo();
			infos.put(vertex, info);
		}
		return info;
	}

	public void visit(GraphPathVisitor<E, V> visitor, V destination) {
		doVisit(graph, visitor, graph.getVertex(destination));
	}

	/**
	 * Used the acumulated info to visit
	 * 
	 * @param graph
	 * @param visitor
	 */
	protected void doVisit(Graph<E, V> graph, GraphPathVisitor<E, V> visitor, Vertex<V, E> goal) {

		visitor.beginVisitGraph(graph);

		doVisitVertex(visitor, goal);

		visitor.endVisitGraph(graph);

	}

	protected void doVisitVertex(GraphPathVisitor<E, V> visitor, Vertex<V, E> v) {

		visitor.beginVisitVertex(v.getObject());

		if (info(v).prev != null) {

			doVisitVertex(visitor, info(v).prev);
		}

		visitor.endVisitVertex(v.getObject());

	}
}
