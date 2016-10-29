/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.phases.ScopeDelimiter;

/**
 * 
 */
public final class BlockNode extends LenseAstNode implements ScopeDelimiter{

	
	public BlockNode(){}

	@Override
	public String getScopeName() {
		return "block";
	}


}
