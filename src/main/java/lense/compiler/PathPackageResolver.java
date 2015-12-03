/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import compiler.CompilationUnit;

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
		Path packagePath = sourcePath.relativize(Paths.get(compilationUnit.getOrigin())).getParent();
		
		if (packagePath== null){
			return "";
		}
		String relative = sourcePath.relativize(Paths.get(compilationUnit.getOrigin())).getParent().toString();
		
		
		return relative.replace(File.separatorChar, '.');
		
	}

}
