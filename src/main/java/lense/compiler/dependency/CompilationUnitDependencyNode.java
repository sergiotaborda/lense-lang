/**
 * 
 */
package lense.compiler.dependency;

import compiler.CompiledUnit;
import lense.compiler.ast.LenseAstNode;
/**
 * 
 */
public class CompilationUnitDependencyNode implements Dependency{

	private CompiledUnit unit;
	private String name;
	
	public CompilationUnitDependencyNode(CompiledUnit unit, String name) {
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
		return other instanceof CompilationUnitDependencyNode && ((CompilationUnitDependencyNode)other).name.equals(this.name);
	}
	
	public int hashCode (){
		return name.hashCode();
	}

	/**
	 * @param node
	 */
	public void setUnit(CompiledUnit unit) {
		this.unit= unit;
	}


	@Override
	public String getDependencyIdentifier() {
		return name;
	}


	
}
