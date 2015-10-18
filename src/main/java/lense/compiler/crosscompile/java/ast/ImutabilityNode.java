/**
 * 
 */
package lense.compiler.crosscompile.java.ast;



/**
 * 
 */
public class ImutabilityNode extends JavaAstNode {

	private Imutability imutability = Imutability.Mutable;
	
	public ImutabilityNode (){}

	/**
	 * Constructor.
	 * @param mutable
	 */
	public ImutabilityNode(Imutability imutability) {
		this.imutability = imutability;
	}

	public Imutability getImutability() {
		return imutability;
	}

}
