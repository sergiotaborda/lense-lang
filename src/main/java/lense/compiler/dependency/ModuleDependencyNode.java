/**
 * 
 */
package lense.compiler.dependency;


import lense.compiler.modules.ModuleDescription;
/**
 * 
 */
public class ModuleDependencyNode implements Dependency {

	private ModuleDescription  module;
	private String name;
	
	public ModuleDependencyNode(ModuleDescription module) {
		super();
		this.module = module;
		this.name = module.getName();
	}

	public String getName() {
		return name;
	}
	
	public ModuleDescription getModuleDescription() {
		return module;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object other){
		return other instanceof ModuleDependencyNode && ((ModuleDependencyNode)other).name.equals(this.name);
	}
	
	public int hashCode (){
		return name.hashCode();
	}

	/**
	 * @param node
	 */
	public void setModuleDescription(ModuleDescription module) {
		this.module= module;
	}

	
	@Override
	public String getDependencyIdentifier() {
		return name;
	}


	
}
