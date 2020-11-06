/**
 * 
 */
package lense.compiler.type;

/**
 * 
 */
public enum LenseUnitKind implements TypeKind{

	Class,
	ValueClass,
	Interface,
	Annotation,
	Enum,
	Enhancement,
	Object,
	Peer,
	Module
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
