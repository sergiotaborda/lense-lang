package lense.compiler;

import compiler.filesystem.SourceFolder;

public final class FileLocations {

	private SourceFolder targetFolder;
	private SourceFolder nativeFolder;
	private SourceFolder sourceFolder;
    private SourceFolder modulesFolder;

	public FileLocations(SourceFolder targetFolder, SourceFolder nativeFolder, SourceFolder sourceFolder , SourceFolder modulesFolder) {
		super();
		this.targetFolder = targetFolder;
		this.nativeFolder = nativeFolder;
		this.sourceFolder = sourceFolder;
		this.modulesFolder = modulesFolder;
	}
	
	public SourceFolder getNativeFolder() {
		return nativeFolder;
	}

	public SourceFolder getTargetFolder() {
		return targetFolder;
	}

    public SourceFolder getSourceFolder() {
        return sourceFolder;
    }

    public SourceFolder getModulesFolder() {
        return modulesFolder;
    }

}
