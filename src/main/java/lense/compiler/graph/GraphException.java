/**
 * 
 */
package lense.compiler.graph;

/**
 * 
 */
public class GraphException extends RuntimeException {

	private static final long serialVersionUID = 6761768367167495540L;

	
	public GraphException (){}


	/**
	 * Constructor.
	 * @param string
	 */
	public GraphException(String message) {
		super(message);
	}
	
}
