/**
 * 
 */
package lense.compiler.type;

/**
 * 
 */
public enum LenseUnitKind implements TypeKind{

	Unknown,
	Class,
	ValueClass,
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
		return this == ValueClass;
	}

	public boolean isEnhancement() {
		return this == Enhancement;
	}
	
}
