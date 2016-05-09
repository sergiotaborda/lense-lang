package lense.compiler;

import java.io.File;

public class FileLocations {

	private File targetFolder;
	private File nativeFolder;
	

	public FileLocations(File targetFolder, File nativeFolder) {
		super();
		this.targetFolder = targetFolder;
		this.nativeFolder = nativeFolder;
	}
	
	public File getNativeFolder() {
		return nativeFolder;
	}

	public File getTargetFolder() {
		return targetFolder;
	}

}
