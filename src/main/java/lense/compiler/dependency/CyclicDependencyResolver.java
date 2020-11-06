package lense.compiler.dependency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lense.compiler.graph.Graph;
import lense.compiler.graph.Graph.Vertex;

public class CyclicDependencyResolver {

	public <E,V> Optional<List<Vertex<V,E>>> resolveOutjacentCycle(Graph<E, V> graph) {
		return resolveCycle(graph, v -> v.getOutjacentEdges().stream().map(e -> e.getTargetVertex()).collect(Collectors.toSet()));
	}
	
	public<E,V> Optional<List<Vertex<V, E>>> resolveIncidentCycle(Graph<E, V> graph) {
		return resolveCycle(graph, v -> v.getIncidentEdges().stream().map(e -> e.getSourceVertex()).collect(Collectors.toSet()));
	}

	private <E,V> Optional<List<Vertex<V, E>>> resolveCycle(
			Graph<E, V> graph, 
			Function<Vertex<V, E>, Set<Vertex<V, E>>> extractor) {
		
		var whiteSet = new HashSet<>(graph.getVertices());
		var backSet = new HashSet<Vertex<V, E>>();
		var greySet = new HashSet<Vertex<V, E>>();
		
		var traceMap = new HashMap<Vertex<V, E>,Vertex<V, E>>();
		
		while(!whiteSet.isEmpty()) {
			// take any element
			var vertex = whiteSet.iterator().next();
			
			traceMap.put(vertex, null);
			var cycle = deepVisit (null , vertex, extractor, whiteSet, backSet, greySet, traceMap);
			
			if (cycle.isPresent()) {
				return cycle;
			}
		}
		
		return Optional.empty(); 
	}

	private <E,V> Optional<List<Vertex<V, E>>> deepVisit(
			Vertex<V, E> parent, Vertex<V, E> vertex,
			Function<Vertex<V, E>, Set<Vertex<V, E>>> extractor, 
			Set<Vertex<V, E>> whiteSet,
			Set<Vertex<V, E>> backSet,
			Set<Vertex<V, E>> greySet, 
			Map<Vertex<V, E>, Vertex<V, E>> traceMap) {
		
		if (greySet.contains(vertex)) {
			// cycle
		
			var cyclePath = new LinkedList<Vertex<V, E>>();
			
			cyclePath.add(vertex);
			cyclePath.addFirst(parent);
			
			var u = traceMap.get(parent);
			while (!u.equals(vertex)) {
				cyclePath.addFirst(u);
				u = traceMap.get(u);
			}
			
			cyclePath.addFirst(u);
			
			return Optional.of(cyclePath);
		}
		
		if (whiteSet.remove(vertex)) {
			greySet.add(vertex);
			
			var children = extractor.apply(vertex);
			
			for (var v : children) {
				
				traceMap.put(v, vertex);
				var cycle = deepVisit (vertex, v, extractor, whiteSet, backSet, greySet,traceMap);
					
				if (cycle.isPresent()) {
					return cycle;
				}
			}
			
			greySet.remove(vertex);

			backSet.add(vertex);
			
		}  
		
		return Optional.empty();
		

	}
}
