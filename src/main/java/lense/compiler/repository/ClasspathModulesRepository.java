package lense.compiler.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFolder;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.modules.JarModule;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleUnit;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.utils.Strings;

public class ClasspathModulesRepository implements ModulesRepository {

	private SourceFolder baseFolder;
	private Map<ModuleIdentifier,  lense.compiler.modules.ModuleUnit> map = new HashMap<>();
	
	public SourceFolder getBase(){
		return baseFolder;
	}
	
	public ClasspathModulesRepository(SourceFolder folder){
		this.baseFolder = folder;
		
		var fileSystem = DiskSourceFileSystem.instance();
		
		try {
			
			
			for (var jarFile : folder.children(f -> f.isFile() && f.asFile().getName().endsWith(".jar"))){
				
				
				EditableModuleDescriptor descriptor = new EditableModuleDescriptor();

				try(java.util.jar.JarFile jar = new java.util.jar.JarFile(fileSystem.convertToFile(jarFile))){
	
	                java.util.Enumeration<java.util.jar.JarEntry> enumEntries = jar.entries();
	                while (enumEntries.hasMoreElements()) {
	                    java.util.jar.JarEntry file =  enumEntries.nextElement();

	                    if (file.getName().equals("module.properties")){
	                        try (java.io.InputStream is = jar.getInputStream(file)){
	                            Properties pp = new Properties();
	                            pp.load(is);
	                            
	                            var version = Version.valueOf(pp.getProperty("module.version"));
	                            var name= pp.getProperty("module.name");
	                            
	                            var dependencies = pp.getProperty("module.requires");
	                            
	                            if(dependencies != null) {
	                            	for (var dep : Strings.split(dependencies,";")){
	                            		var req = Strings.split(dep,"@");
	                            		descriptor.addRequiredModule(new EditableModuleDescriptor(req[0], Version.valueOf(req[1])));
	                            	}
	                            	
	                            }
	                            
	                            descriptor.setVersion(version);
	                            descriptor.setName(name);
	                            
	                            map.put(descriptor.getIdentifier(), new JarModule(descriptor,new JarTypeRepository(jarFile.asFile())));
	                        }
	                    }
	                }
	                
				}

			}
			
			
			
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}


	@Override
	public Optional<ModuleUnit> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
		return Optional.ofNullable(this.map.get(identifier));
	}

	@Override
	public List<SourceFolder> getClassPath() {
		return List.of(baseFolder);
	}



}
