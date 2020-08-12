package lense.compiler;

import compiler.filesystem.SourcePath;
import lense.compiler.utils.Strings;

public class PackageSourcePathUtils {

	
	public static SourcePath  fromPackageName(String name) {
		return SourcePath.of(Strings.split(name, "."));
	}
	
	public static String toPackageName(SourcePath path) {
		return path.join(".");
	}
}
