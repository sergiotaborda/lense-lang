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
	TypeClass,
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
	
	public boolean isTypeClass() {
		return this == TypeClass;
	}

	public boolean  isClass() {
		return this == Class;
	}
	
}
