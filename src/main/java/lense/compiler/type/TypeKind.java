/**
 * 
 */
package lense.compiler.type;

/**
 * 
 */
public interface TypeKind {

	boolean isInterface();

	boolean isObject();

	boolean isEnhancement();

	boolean isValue();
	
	boolean isTypeClass();
}
