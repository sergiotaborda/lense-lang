package lense.compiler.dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;

public class TestCycle {

	
	DependencyNode v1 = new DependencyNode(null, "1");
	DependencyNode v2 = new DependencyNode(null, "2");
	DependencyNode v3 = new DependencyNode(null, "3");
	DependencyNode v4 = new DependencyNode(null, "4");
	DependencyNode v5 = new DependencyNode(null, "5");
	DependencyNode v6 = new DependencyNode(null, "6");
	
	DependencyRelation r = new DependencyRelation(DependencyRelationship.Structural);
			
	@Test
	public void testOutjacentCycle () {
		
		var g = new DependencyGraph();

		g.addEdge(r, v1, v2);
		g.addEdge(r, v1, v3);
		g.addEdge(r, v2, v3);
		g.addEdge(r, v4, v1);
		g.addEdge(r, v4, v5);
		g.addEdge(r, v5, v6);
		g.addEdge(r, v6, v4);
		
		var cycle = new CyclicDependencyResolver().resolveOutjacentCycle(g);
		
		assertTrue(cycle.isPresent());
		
		var names = cycle.get().stream().map(v -> v.getObject().getName()).collect(Collectors.joining("->"));
		
		assertEquals("4->5->6->4", names);
	}
	
	@Test
	public void testIncidenttCycle () {
		
		var g = new DependencyGraph();

		g.addEdge(r, v1, v2);
		g.addEdge(r, v1, v3);
		g.addEdge(r, v2, v3);
		g.addEdge(r, v4, v1);
		g.addEdge(r, v4, v5);
		g.addEdge(r, v5, v6);
		g.addEdge(r, v6, v4);
		
		var cycle = new CyclicDependencyResolver().resolveIncidentCycle(g);
		
		assertTrue(cycle.isPresent());
		
		var names = cycle.get().stream().map(v -> v.getObject().getName()).collect(Collectors.joining("->"));
		
		assertEquals("4->6->5->4", names);
	}
}
