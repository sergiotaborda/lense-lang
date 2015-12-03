/**
 * 
 */
package lense.compiler;

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