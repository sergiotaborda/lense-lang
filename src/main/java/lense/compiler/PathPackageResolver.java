/**
 * 
 */
package lense.compiler;

import compiler.CompilationUnit;
import compiler.filesystem.SourcePath;
import lense.compiler.typesystem.PackageResolver;

/**
 * 
 */
public class PathPackageResolver implements PackageResolver {

	private SourcePath sourcePath;

	/**
	 * Constructor.
	 * @param path
	 */
	public PathPackageResolver(SourcePath sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveUnitPackageName(CompilationUnit compilationUnit) {
		
		if(compilationUnit.getOrigin() == null) {
			return "";
		}
		
		var packagePath = sourcePath.relativize(compilationUnit.getOrigin()).getParent();
		
		if (packagePath == null){
			return "";
		}
		
		return packagePath.join(".");
		

		
	}

}
