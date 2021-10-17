/**
 * 
 */
package lense.compiler.typesystem;

/**
 * 
 */
public enum Visibility implements Comparable<Visibility> {
	
	Undefined,
	Protected,
	Private,
	Public;
	
	public boolean isMoreVisibleThan(Visibility other) {
		return this.compareTo(other) > 0;
	}
	
	
	public boolean isLessVisibleThan(Visibility other) {
		return this.compareTo(other) < 0;
	}
}
