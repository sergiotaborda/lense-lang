/**
 * 
 */
package lense.compiler.dependency;

import lense.compiler.ast.LenseAstNode;

import compiler.CompiledUnit;
/**
 * 
 */
public class DependencyNode {

	private CompiledUnit unit;
	private String name;
	
	public DependencyNode(CompiledUnit unit, String name) {
		super();
		this.unit = unit;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public LenseAstNode getAstNode() {
		return (LenseAstNode) unit.getAstRootNode();
	}
	
	public CompiledUnit getCompiledUnit() {
		return unit;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object other){
		return other instanceof DependencyNode && ((DependencyNode)other).name.equals(this.name);
	}

	/**
	 * @param node
	 */
	public void setUnit(CompiledUnit unit) {
		this.unit= unit;
	}

	
}
