package lense.compiler;

import compiler.filesystem.SourceFile;

public record NativeSourceInfo(SourceFile nativeCompiledFile, SourceFile originalSourceFile) {
	
}