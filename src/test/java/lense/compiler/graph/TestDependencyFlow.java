package lense.compiler.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.stream.Collectors;

import org.junit.Test;

import junit.framework.Assert;
import lense.compiler.asm.TypeDefinitionInfo;
import lense.compiler.dependency.CompilationUnitDependencyNode;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;

public class TestDependencyFlow {


	CompilationUnitDependencyNode A = new CompilationUnitDependencyNode(null, "A");
	CompilationUnitDependencyNode B = new CompilationUnitDependencyNode(null, "B");
	CompilationUnitDependencyNode C = new CompilationUnitDependencyNode(null, "C");
	CompilationUnitDependencyNode D = new CompilationUnitDependencyNode(null, "D");
	CompilationUnitDependencyNode E = new CompilationUnitDependencyNode(null, "E");
	CompilationUnitDependencyNode F = new CompilationUnitDependencyNode(null, "F");
	CompilationUnitDependencyNode G = new CompilationUnitDependencyNode(null, "G");
	CompilationUnitDependencyNode H = new CompilationUnitDependencyNode(null, "H");
	CompilationUnitDependencyNode I = new CompilationUnitDependencyNode(null, "I");
	CompilationUnitDependencyNode J = new CompilationUnitDependencyNode(null, "J");
	CompilationUnitDependencyNode K = new CompilationUnitDependencyNode(null, "K");
	CompilationUnitDependencyNode U = new CompilationUnitDependencyNode(null, "U");
	CompilationUnitDependencyNode V = new CompilationUnitDependencyNode(null, "V");

	DependencyRelation r = new DependencyRelation(DependencyRelationship.Structural);

	@Test
	public void testFollowTransversor () {

		var g = new DependencyGraph<CompilationUnitDependencyNode>();

		g.addEdge(r, A, B);
		g.addEdge(r, A, G);
		g.addEdge(r, B, H);
		g.addEdge(r, B, C);
		g.addEdge(r, H, I);
		g.addEdge(r, I, J);
		g.addEdge(r, C, J);
		g.addEdge(r, C, D);
		g.addEdge(r, D, E);
		g.addEdge(r, D, K);
		g.addEdge(r, E, F);
		g.addEdge(r, E, A);
		g.addEdge(r, U, V);
		
		var expected = new LinkedList<String>();

		expected.add("G");
		expected.add("K");
		expected.add("A'");
		expected.add("F");
		expected.add("E");
		expected.add("D");
		expected.add("J'");
		expected.add("C");
		expected.add("J");
		expected.add("I");
		expected.add("H");
		expected.add("B");
		expected.add("A");
		expected.add("V");
		expected.add("U");
		
		var infoTransversor = new FollowTransversor<DependencyRelation,CompilationUnitDependencyNode>();

		infoTransversor.addListener(new GraphTranverseListener<>() {

			@Override
			public void beginVertex(VertexTraversalEvent<CompilationUnitDependencyNode, DependencyRelation> e) {

				var f = (FollowVertexTraversalEvent)e;
				
				var info = e.getVertex().getObject();

				var name = info.getName() + (f.isFirstcross() ? "" : "'");

				Assert.assertEquals(expected.pop(), name); ;
			}

		});

		infoTransversor.transverse(g, A);
	}


}
