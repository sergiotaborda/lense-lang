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

import lense.compiler.graph.Graph.Vertex;

public class CyclicDependencyResolver {

	public Optional<List<Vertex<DependencyNode, DependencyRelation>>> resolveOutjacentCycle(DependencyGraph graph) {
		return resolveCycle(graph, v -> v.getOutjacentEdges().stream().map(e -> e.getTargetVertex()).collect(Collectors.toSet()));
	}
	
	public Optional<List<Vertex<DependencyNode, DependencyRelation>>> resolveIncidentCycle(DependencyGraph graph) {
		return resolveCycle(graph, v -> v.getIncidentEdges().stream().map(e -> e.getSourceVertex()).collect(Collectors.toSet()));
	}

	private Optional<List<Vertex<DependencyNode, DependencyRelation>>> resolveCycle(
			DependencyGraph graph, 
			Function<Vertex<DependencyNode, DependencyRelation>, Set<Vertex<DependencyNode, DependencyRelation>>> extractor) {
		
		var whiteSet = new HashSet<>(graph.getVertices());
		var backSet = new HashSet<Vertex<DependencyNode, DependencyRelation>>();
		var greySet = new HashSet<Vertex<DependencyNode, DependencyRelation>>();
		
		var traceMap = new HashMap<Vertex<DependencyNode, DependencyRelation>,Vertex<DependencyNode, DependencyRelation>>();
		
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

	private Optional<List<Vertex<DependencyNode, DependencyRelation>>> deepVisit(
			Vertex<DependencyNode, DependencyRelation> parent, Vertex<DependencyNode, DependencyRelation> vertex,
			Function<Vertex<DependencyNode, DependencyRelation>, Set<Vertex<DependencyNode, DependencyRelation>>> extractor, 
			Set<Vertex<DependencyNode, DependencyRelation>> whiteSet,
			Set<Vertex<DependencyNode, DependencyRelation>> backSet,
			Set<Vertex<DependencyNode, DependencyRelation>> greySet, 
			Map<Vertex<DependencyNode, DependencyRelation>, Vertex<DependencyNode, DependencyRelation>> traceMap) {
		
		if (greySet.contains(vertex)) {
			// cycle
		
			var cyclePath = new LinkedList<Vertex<DependencyNode, DependencyRelation>>();
			
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
