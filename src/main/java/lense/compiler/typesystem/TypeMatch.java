/**
 * 
 */
package lense.compiler.typesystem;

/**
 * 
 */
public enum TypeMatch {

	NoMatch,
	Promote,
	UpCast,
	Exact;
	
	public boolean matches() {
		return this != NoMatch;
	}
	
	public boolean isExact() {
		return this == Exact;
	}
	
	public boolean isUpCast() {
		return this == UpCast;
	}
	
	public TypeMatch and(TypeMatch other) {
		if (this == NoMatch || other == NoMatch) {
			return NoMatch;
		} else if (this == other) {
			return this;
		} else if (this == Exact) {
			return other;
		} 
		
		return Min(this, other);
	}


	private TypeMatch Min(TypeMatch a, TypeMatch b) {
		if (a.compareTo(b) > 0) {
			return b;
		}
		return a;
	}
}
