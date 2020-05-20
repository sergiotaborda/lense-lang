/**
 * 
 */
package lense.compiler;

import lense.compiler.ast.QualifiedNameNode;

/**
 * 
 */
public class Import {

	
	private QualifiedNameNode name;
	private boolean isUsed = false;
	private boolean isMemberCalled = false; 
	private boolean isSuper = false; 
	private boolean container;
	private String alias;
	
	public Import (QualifiedNameNode name, String alias, boolean container){
		this.name = name;
		this.container= container;
		this.alias = alias;
	}

	public String toString(){
		return name.getName();
	}
	
	public boolean equals(Object other){
		return other instanceof Import && ((Import)other).name.equals(name);
	}
	
	public int hashCode(){
		return name.hashCode();
	}
	
	/**
	 * @param name2
	 * @return
	 */
	public static Import all(QualifiedNameNode name) {
		return new Import(name, null, true);
	}
	
	public static Import singleType(QualifiedNameNode name, String alias) {
		return new Import(name, alias, false);
	}
	
	public boolean isContainer(){
		return container;
	}

	/**
	 * @param b
	 */
	public Import setUsed(boolean used) {
		this.isUsed = used;
		return this;
	}

	/**
	 * @param name2
	 * @return
	 */
	public boolean endsWith(String n) {
		return name.getLast().equals(new QualifiedNameNode(n));
	}

	/**
	 * @return
	 */
	public QualifiedNameNode getTypeName() {
		return  name;
	}

	public String getMatchAlias(){
		return alias != null ? alias : name.getLast().getName();
	}
	/**
	 * @return
	 */
	public boolean isUsed() {
		return isUsed;
	}

	public boolean isMemberCalled() {
		return isMemberCalled;
	}

	public Import setMemberCalled(boolean isMemberCalled) {
		this.isMemberCalled = isMemberCalled;
		return this;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getAlias(){
		return alias;
	}

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}
	

}
