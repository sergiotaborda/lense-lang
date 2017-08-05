/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.nio.file.Path;

import compiler.CompilationUnit;
import lense.compiler.typesystem.PackageResolver;

/**
 * 
 */
public class PathPackageResolver implements PackageResolver {

	private Path sourcePath;

	/**
	 * Constructor.
	 * @param path
	 */
	public PathPackageResolver(Path sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String resolveUnitPackageName(CompilationUnit compilationUnit) {
		Path packagePath = sourcePath.relativize(compilationUnit.getOrigin()).getParent();
		
		if (packagePath== null){
			return "";
		}
		String relative = sourcePath.relativize(compilationUnit.getOrigin()).getParent().toString();
		
		
		return relative.replace(File.separatorChar, '.');
		
	}

}
