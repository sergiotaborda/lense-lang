package lense.compiler.dependency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.junit.Test;

public class TestCycle {

	
	CompilationUnitDependencyNode v1 = new CompilationUnitDependencyNode(null, "1");
	CompilationUnitDependencyNode v2 = new CompilationUnitDependencyNode(null, "2");
	CompilationUnitDependencyNode v3 = new CompilationUnitDependencyNode(null, "3");
	CompilationUnitDependencyNode v4 = new CompilationUnitDependencyNode(null, "4");
	CompilationUnitDependencyNode v5 = new CompilationUnitDependencyNode(null, "5");
	CompilationUnitDependencyNode v6 = new CompilationUnitDependencyNode(null, "6");
	
	DependencyRelation r = new DependencyRelation(DependencyRelationship.Structural);
			
	@Test
	public void testOutjacentCycle () {
		
		var g = new DependencyGraph<CompilationUnitDependencyNode>();

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
		
		var g = new DependencyGraph<CompilationUnitDependencyNode>();

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
