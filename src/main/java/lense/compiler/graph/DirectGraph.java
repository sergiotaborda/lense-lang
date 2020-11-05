package lense.compiler.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lense.compiler.dependency.CyclicDependencyResolver;
import lense.compiler.graph.VertextInfoManager.VertexInfo;

/**
 * 
 * @param <E> object at the edge
 * @param <V> object class at the vertex.
 */
public class DirectGraph<E,V> implements Graph<E,V>{


	private class BeanEdge<Va, Ea> implements Edge<Va, Ea>{

		private Ea object;
		private Vertex<Va, Ea> targetVertex;
		private double cost;
		private Vertex<Va, Ea> sourceVertex;

		public BeanEdge(Ea object, Vertex<Va, Ea> sourceVertex, Vertex<Va, Ea> targetVertex, double cost) {
			this.object = object;
			this.targetVertex = targetVertex;
			this.sourceVertex = sourceVertex;
			this.cost = cost;
		}



		@Override
		public Ea getObject() {
			return object;
		}

		@Override
		public Vertex<Va, Ea> getTargetVertex() {
			return targetVertex;
		}

		@Override
		public Vertex<Va, Ea> getSourceVertex() {
			return sourceVertex;
		}

		@Override
		public double getCost() {
			return cost;
		}

		public String toString(){
			return object.toString();
		}

		public int hashCode(){
			return object == null ? 0 : object.hashCode();
		}
		
		public boolean equals(Object other){
			return other instanceof BeanEdge && equalsEdge((BeanEdge) other); 
		}

		/**
		 * @param other
		 * @return
		 */
		private boolean equalsEdge(BeanEdge other) {
			return object != null && this.object.equals (other.object) 
					&& this.sourceVertex.equals(other.sourceVertex) 
					&& this.targetVertex.equals(other.targetVertex)
					&& Double.compare(this.cost, other.cost) == 0; 
		}
	}

	private class BeanVertex<Va, Ea> implements Vertex<Va, Ea> {

		private Va object;
		private List<Edge<Va,Ea>> outjacentEdges = new LinkedList<Edge<Va,Ea>>();
		private List<Edge<Va,Ea>> incidentEdges = new LinkedList<Edge<Va,Ea>>();

		private double dist;
		public Vertex<Va, Ea> previous;
		private int scratch;

		public BeanVertex(Va object){
			this.object = object;
			reset();
		}

		public final void reset(){
			dist = Double.MAX_VALUE;
			previous = null;
			scratch = 0;
		}

		@Override
		public Va getObject() {
			return object;
		}

		public String toString(){
			return String.valueOf(this.object);
		}

		@Override
		public List<lense.compiler.graph.Graph.Edge<Va,Ea>> getOutjacentEdges() {
			return outjacentEdges;
		}

		@Override
		public List<lense.compiler.graph.Graph.Edge<Va, Ea>> getIncidentEdges() {
			return incidentEdges;
		}
		
		public int hashCode(){
			return object == null ? 0 : object.hashCode();
		}
		
		public boolean equals(Object other){
			return other instanceof BeanVertex && equalsVertex((BeanVertex) other); 
		}

		/**
		 * @param other
		 * @return
		 */
		private boolean equalsVertex(BeanVertex other) {
			return this.object != null && this.object.equals (other.object); 
		}

	}

	private final Map<V, Vertex<V, E>> vertexes = new HashMap<V, Vertex<V, E>>();
	private final Set<Edge<V, E>> edges = new HashSet<Edge<V, E>>();

	/**
	 * 
	 */
	public DirectGraph (){}

	/**
	 * Constructor.
	 * @param directGraph
	 */
	public DirectGraph(DirectGraph<E, V> other) {

		for (Vertex<V,E> v : other.vertexes.values()){
			for (Edge<V,E> edge: v.getOutjacentEdges()){
				this.addEdge(edge.getObject(), v.getObject(), edge.getTargetVertex().getObject(), edge.getCost());
			}
		}

	}

	@Override
	public Collection<lense.compiler.graph.Graph.Vertex<V, E>> getVertices() {
		return new HashSet<lense.compiler.graph.Graph.Vertex<V, E>>(vertexes.values());
	}


	@Override
	public Collection<lense.compiler.graph.Graph.Edge<V, E>> getEdges() {
		return edges;
	}

	public void addEdge(E edgeObject,
			V sourceVertex,
			V targetVertex) {
		this.addEdge(edgeObject, sourceVertex, targetVertex,1);
		
	}
	

	@Override
	public void addEdge(E edgeObject,
			V sourceVertex,
			V targetVertex, double cost) {

		Vertex<V, E> v = vertexes.get(sourceVertex);

		if (v == null){
			v = new BeanVertex<V, E>(sourceVertex);
			vertexes.put(sourceVertex,v);
		}

		Vertex<V, E> w = vertexes.get(targetVertex);

		if (w == null){
			w = new BeanVertex<V, E>(targetVertex);
			vertexes.put(targetVertex, w);
		}

		BeanEdge<V, E> edge = new BeanEdge<V, E>(edgeObject, v , w, cost);
		v.getOutjacentEdges().add(edge);
		w.getIncidentEdges().add(edge);

		this.edges.add(edge);
	}

	@Override
	public lense.compiler.graph.Graph.Vertex<V, E> getVertex(V vertex) {
		return this.vertexes.get(vertex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeVertex(Vertex<V, E> vertex) {

		for( Edge <V, E> edge :vertex.getIncidentEdges()){

			edge.getSourceVertex().getOutjacentEdges().remove(edge);

			this.edges.remove(edge);
		}

		for( Edge <V, E> edge :vertex.getOutjacentEdges()){

			edge.getSourceVertex().getIncidentEdges().remove(edge);

			this.edges.remove(edge);
		}

		vertexes.remove(vertex.getObject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeEdge(lense.compiler.graph.Graph.Edge<V, E> edge) {
		edge.getSourceVertex().getOutjacentEdges().remove(edge);
		edge.getTargetVertex().getIncidentEdges().remove(edge);
		this.edges.remove(edge);
	}

	public void filter(GraphFilter<V, E> filter){
		Collection<Vertex<V, E>> all = this.getVertices();

		VertextInfoManager manager = new VertextInfoManager();

		if (!all.isEmpty()){


			LinkedList<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();

			// compute ingree

			for (Vertex<V, E> v: all ){
				for (Edge<V, E> e : v.getOutjacentEdges()){
					manager.info(e.getTargetVertex()).incrementScratch();
				}
			}

			// enqueue those with ingree zero
			for (Vertex<V, E> v : all){
				if (manager.info(v).hasZeroScratch()){
					q.add(v);
				}
			}

			while (!q.isEmpty()){

				Vertex<V, E> v = q.remove();

				if(! filter.accepts(v)){
					this.removeVertex(v);
				} else {

					Collection<Edge<V,E>> toremove = new HashSet<Edge<V,E>>();

					for (Edge<V, E> e : v.getOutjacentEdges() ){


						if (!filter.accepts(e)){
							toremove.add(e);
						} 

						Vertex<V, E> w = e.getTargetVertex();
						double cvw = e.getCost();

						VertexInfo infoW = manager.info(w);

						infoW.decrementScratch();
						if ( infoW.hasZeroScratch() ) {
							q.add(w);
						}

						VertexInfo infoV = manager.info(v);


						if (Double.compare(infoV.dist, Double.MAX_VALUE) == 0){
							continue;
						}
						if (Double.compare(infoW.dist, infoV.dist + cvw) > 0){
							infoW.dist = infoV.dist + cvw;
							infoW.prev = v;
							infoW.connectingEdge = e;
						}
					}

					for (Edge<V,E> e : toremove){
						this.removeEdge(e);
					}

				}



			}


			for (Iterator<Map.Entry<V , Vertex<V, E>>> it = this.vertexes.entrySet().iterator(); it.hasNext(); ){
				Vertex<V, E> v = it.next().getValue();

				if (v.getIncidentEdges().isEmpty() && v.getOutjacentEdges().isEmpty()){
					it.remove();
				}
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean containsEdge(E edge) {
		for (Edge<V,E> e : this.edges){
			if (e.getObject().equals(edge)){
				return true;
			}
		}
		return false;
	}
	
	protected DirectGraph<E, V> duplicateMe(){
		return new DirectGraph<E,V>(this);
	}

	public List<Graph<E,V>> split(){


		List<Graph<E,V>> result = new ArrayList<Graph<E,V>>();

		DirectGraph<E, V> copy  = duplicateMe();

		Collection<Vertex<V, E>> all = copy.getVertices();

		
		if (!all.isEmpty()){


			LinkedList<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();

			// compute ingree

			Set<Vertex<V, E>> toVisit = new HashSet<Graph.Vertex<V,E>>(all);

			while(!toVisit.isEmpty()) {
				VertextInfoManager manager = new VertextInfoManager();

				DirectGraph<E, V> current = new DirectGraph<E, V>();
				
				result.add(current);
				
				for (Vertex<V, E> v: toVisit ){
					for (Edge<V, E> e : v.getOutjacentEdges()){
						manager.info(e.getTargetVertex()).incrementScratch();
					}
				}

				// enqueue those with ingree zero
				for (Vertex<V, E> v : toVisit){
					if (manager.info(v).hasZeroScratch()){
						q.add(v);
					}
				}


				if (q.isEmpty()) {
					// there is a cicle.
					// pick one
					q.add(toVisit.iterator().next());
				}

				while(!q.isEmpty()){

					Vertex<V, E> v = q.remove();

					if (manager.info(v).visited ==0){

						manager.info(v).visited++;
						toVisit.remove(v);

						for (Edge<V, E> e : v.getOutjacentEdges() ){

							Vertex<V, E> w = e.getTargetVertex();
						
							current.addEdge(e.getObject(), v.getObject(), w.getObject(), e.getCost());
							
							double cvw = e.getCost();

							VertexInfo infoW = manager.info(w);

							infoW.decrementScratch();
							if (infoW.hasZeroScratch()) {
								q.add(w);
							}

							VertexInfo infoV = manager.info(v);


							if (Double.compare(infoV.dist, Double.MAX_VALUE) == 0){
								continue;
							}
							if (Double.compare(infoW.dist, infoV.dist + cvw) > 0){
								infoW.dist = infoV.dist + cvw;
								infoW.prev = v;
								infoW.connectingEdge = e;
							}
						}
					}
				}
			}
		}

		return result;

	}


	/**
	 * @param graphVisitor
	 */
	public void visit(GraphVisitor<E, V> visitor) {

		Collection<Vertex<V, E>> all = this.getVertices();

		VertextInfoManager manager = new VertextInfoManager();

		if (!all.isEmpty()){


			LinkedList<Vertex<V, E>> q = new LinkedList<Vertex<V, E>>();

			// compute ingree

			for (Vertex<V, E> v: all ){
				for (Edge<V, E> e : v.getOutjacentEdges()){
					manager.info(e.getTargetVertex()).incrementScratch();
				}
			}

			// enqueue those with ingree zero
			for (Vertex<V, E> v : all){
				if (manager.info(v).hasZeroScratch()){
					q.add(v);
				}
			}


			if (q.isEmpty()) {
				// there is a cicle.
				// pick one
				q.add(all.iterator().next());
			}

			visitor.onBeginGraph(this);

			Set<Vertex<V, E>> toVisit = new HashSet<Graph.Vertex<V,E>>(all);
			Set<Vertex<V, E>> visited = new HashSet<Graph.Vertex<V,E>>();

			while(!q.isEmpty()){

				Vertex<V, E> v = q.remove();

				if (visited.add(v)){
					visitor.onBeginVertex(v);
					toVisit.remove(v);

					for (Edge<V, E> e : v.getOutjacentEdges() ){

						visitor.onEdge(e);

						Vertex<V, E> w = e.getTargetVertex();
						double cvw = e.getCost();

						VertexInfo infoW = manager.info(w);

						infoW.decrementScratch();
						if ( infoW.hasZeroScratch() ) {
							q.add(w);
						}

						VertexInfo infoV = manager.info(v);

						if (Double.compare(infoV.dist, Double.MAX_VALUE) == 0){
							continue;
						}
						if (Double.compare(infoW.dist, infoV.dist + cvw) > 0){
							infoW.dist = infoV.dist + cvw;
							infoW.prev = v;
							infoW.connectingEdge = e;
						}

					}
					visitor.onEndVertex(v);
				}

			}

			if (!toVisit.isEmpty()){
				throw new CycleFoundException(new CyclicDependencyResolver().resolveIncidentCycle(this).orElse(Collections.emptyList()));
			}

		}

		visitor.onEndGraph(this);


	}
	




}
