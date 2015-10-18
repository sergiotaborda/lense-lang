/**
 * 
 */
package lense.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lense.compiler.CompilationError;
import lense.compiler.SemanticScope;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;

/**
 * 
 */
public class SemanticScope {

	private SemanticScope parent;
	private List<SemanticScope> scopes = new ArrayList<SemanticScope>();
	private Map<String, VariableInfo > variables= new HashMap<String, VariableInfo>();
	private String name;

	
	/**
	 * Constructor.
	 * @param scope
	 */
	public SemanticScope(SemanticScope scope) {
		this.parent = scope;
		scope.addChild(this);
	}
	
	/**
	 * Constructor.
	 * @param scope 
	 * @param name 
	 */
	public SemanticScope(String name, SemanticScope scope) {
		this.name = name;
		this.parent = scope;
		scope.addChild(this);
	}

	/**
	 * Constructor.
	 * @param name2
	 */
	public SemanticScope(String name) {
		this.name = name;
	}

	public String toString(){
		return name;
	}
	/**
	 * @param semanticScope
	 */
	private void addChild(SemanticScope semanticScope) {
		scopes.add(semanticScope);
	}
	



	/**
	 * @param id
	 * @param type
	 */
	public VariableInfo defineVariable(String name, TypeDefinition type) {
		
		if (variables.containsKey(name)){
			throw new CompilationError("Varible " + name + " is already defined in this scope.");
		}
	   final VariableInfo variableInfo = new VariableInfo(name, type, false);
	   variables.put(name, variableInfo);
	   return variableInfo;
	}

	/**
	 * @param string
	 * @return
	 */
	public VariableInfo searchVariable(String name) {
		VariableInfo info = variables.get(name);
		
		if (info == null && parent != null){
			return this.parent.searchVariable(name);
		}
			
		return info;
	}

	/**
	 * @param name
	 * @param any
	 */
	public VariableInfo defineTypeVariable(String name, TypeDefinition type) {
		if (variables.containsKey(name)){
			throw new CompilationError("Type varible " + name + " is already defined in this scope.");
		}
	   final VariableInfo variableInfo = new VariableInfo(name, type, true);
	   variables.put(name, variableInfo);
	   
	   return variableInfo;
	}

	/**
	 * @return
	 */
	public SemanticScope getParent() {
		return parent;
	}

}