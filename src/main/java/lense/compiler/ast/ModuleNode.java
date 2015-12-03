/**
 * 
 */
package lense.compiler.ast;

import lense.compiler.repository.Version;


/**
 * 
 */
public class ModuleNode extends LenseAstNode {

	private String name;
	private VersionNode version;
	private ModuleMembersNode exports;
	private ModuleMembersNode imports;

	/**
	 * @param qualifiedName
	 */
	public void setName(QualifiedNameNode qn) {
		this.name = qn.getName();
	}

	/**
	 * @param versionNode
	 */
	public void setVersion(VersionNode version) {
		this.version = version;
	}

	/**
	 * @param exports
	 */
	public void setExports(ModuleMembersNode exports) {
		this.exports = exports;
	}

	/**
	 * @param imports
	 */
	public void setImports(ModuleMembersNode imports) {
		this.imports = imports;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Version getVersion() {
		return version.getVersion();
	}

	public ModuleMembersNode getImports(){
		return imports;
	}

}
