/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.ast.Imutability;
import lense.compiler.ast.LenseAstNode;


/**
 * 
 */
public class ImutabilityNode extends LenseAstNode {

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
