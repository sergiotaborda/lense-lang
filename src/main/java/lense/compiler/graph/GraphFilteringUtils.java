package lense.compiler.graph;

import java.util.function.Predicate;

import lense.compiler.graph.Graph.Edge;
import lense.compiler.graph.Graph.Vertex;

/**
 * 
 * 
 */
public final class GraphFilteringUtils {


	private GraphFilteringUtils (){}

	/**
	 * Fills a new graph by coping another graph and Predicate to eliminate Edges and Vertices.
	 * @param <E>
	 * @param <V>
	 * @param sourceGraph
	 * @param verticesClassifier
	 * @param edgeClassifier
	 * @return
	 */
	public static <E, V, G extends Graph<E,V> > G  filter(G sourceGraph, Predicate<V> verticesClassifier , Predicate<E> edgeClassifier ) {
		try {
			G targetGraph = (G) sourceGraph.getClass().newInstance();


			for ( Edge<V,E> edge : sourceGraph.getEdges()){

				Vertex<V,E> s = edge.getSourceVertex();
				Vertex<V,E> t = edge.getTargetVertex();

				if ((verticesClassifier.test(s.getObject()) || verticesClassifier.test(t.getObject())) && edgeClassifier.test(edge.getObject())){
					targetGraph.addEdge(edge.getObject(), s.getObject(), t.getObject(), edge.getCost());
				}
			}

			return targetGraph;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <E, V, G extends Graph<E,V> > G  filter(G sourceGraph, Predicate<V> verticesClassifier ) {
		return filter(sourceGraph, verticesClassifier, obj -> true);
	}



}
