/**
 * 
 */
package lense.compiler.context;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class VariableInfo {

	private TypeVariable type;
	private String name;
	private boolean initialized;
	private boolean isTypeVariable;
	private boolean escapes = false;
	private int writeCount = 0;
	private boolean imutable;
	private boolean predefined;
	private AstNode declaringNode;

	/**
	 * Constructor.
	 * @param name
	 * @param type
	 */
	public VariableInfo(String name, TypeVariable type, AstNode declaringNode, boolean isTypeVariable, boolean predefined) {
		this.name = name;
		this.type = type;
		this.isTypeVariable= isTypeVariable;
		this.imutable = false;
		this.predefined = predefined;
		this.declaringNode = declaringNode;
	}
	
	public AstNode getDeclaringNode(){
		return declaringNode;
	}

	public boolean isPredefined(){
		return predefined;
	}
	
	public void markEscapes(){
		escapes = true;
	}
	
	public boolean doesEscape(){
		return escapes;
	}
	
	public void markWrite(){
		writeCount++;
	}

	public boolean isEfectivlyFinal(){
		return writeCount < 2;
	}

	public TypeVariable getTypeVariable() {
		return type;
	}

	/**
	 * Obtains {@link String}.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param b
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean isInitialized(){
		return initialized;
	}


	/**
	 * @return
	 */
	public boolean isTypeVariable() {
		return isTypeVariable;
	}

	public boolean isImutable() {
		return imutable;
	}

	public void setImutable(boolean imutable) {
		this.imutable = imutable;
	}


	
}
