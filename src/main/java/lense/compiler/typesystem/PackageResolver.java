/**
 * 
 */
package lense.compiler.typesystem;

import compiler.CompilationUnit;

/**
 * 
 */
public interface PackageResolver {

	/**
	 * @param compilationUnit
	 * @return
	 */
	public String resolveUnitPackageName(CompilationUnit compilationUnit);

}