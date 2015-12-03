/**
 * 
 */
package lense.compiler.graph;

/**
 * 
 */
public abstract class GraphProperty {

	private String name;
	
	public GraphProperty(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
}
