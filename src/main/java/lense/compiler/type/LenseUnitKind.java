/**
 * 
 */
package lense.compiler.type;

/**
 * 
 */
public enum LenseUnitKind implements TypeKind{

	Class,
	Value,
	Interface,
	Annotation,
	Enum,
	Enhancement,
	Object
	;

	public boolean isInterface() {
		return this == Interface;
	}

	public boolean isObject() {
		return this == Object;
	}
	
	public boolean isValue() {
		return this == Value;
	}

	public boolean isEnhancement() {
		return this == Enhancement;
	}
	
	
}
