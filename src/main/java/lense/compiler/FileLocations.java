package lense.compiler;

import java.io.File;

public final class FileLocations {

	private File targetFolder;
	private File nativeFolder;
	private File sourceFolder;
    private File modulesFolder;

	public FileLocations(File targetFolder, File nativeFolder, File sourceFolder , File modulesFolder) {
		super();
		this.targetFolder = targetFolder;
		this.nativeFolder = nativeFolder;
		this.sourceFolder = sourceFolder;
		this.modulesFolder = modulesFolder;
	}
	
	public File getNativeFolder() {
		return nativeFolder;
	}

	public File getTargetFolder() {
		return targetFolder;
	}

    public File getSourceFolder() {
        return sourceFolder;
    }

    public File getModulesFolder() {
        return modulesFolder;
    }

}
