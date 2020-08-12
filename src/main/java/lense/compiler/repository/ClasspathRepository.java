package lense.compiler.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFolder;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.modules.ModuleIdentifier;
import lense.compiler.modules.ModuleTypeContents;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

public class ClasspathRepository implements TypeRepository, ModulesRepository{

    private List<ModuleTypeContents> modules = new LinkedList<>();
	private Map<TypeSearchParameters, TypeDefinition> types = new HashMap<>();
	private SourceFolder baseFolder;

	public SourceFolder getBase(){
		return baseFolder;
	}
	
	public ClasspathRepository(SourceFolder folder){
		this.baseFolder = folder;
		
		var fileSystem = DiskSourceFileSystem.instance();
		
		try {
			// TODO handle inter module dependencies
			
			for (var jarFile : folder.children(f -> f.isFile() && f.asFile().getName().endsWith(".jar"))){
				ModuleTypeContents moduleRepo = new ModuleTypeContents();

				ByteCodeTypeDefinitionReader reader = new ByteCodeTypeDefinitionReader(moduleRepo);
				
				modules.add(moduleRepo);
				try(java.util.jar.JarFile jar = new java.util.jar.JarFile(fileSystem.convertToFile(jarFile))){
				  //Manifest manifest = jar.getManifest();

			
	                java.util.Enumeration<java.util.jar.JarEntry> enumEntries = jar.entries();
	                while (enumEntries.hasMoreElements()) {
	                    java.util.jar.JarEntry file =  enumEntries.nextElement();

	                    if (file.getName().endsWith(".class")){
	                        try (java.io.InputStream is = jar.getInputStream(file)){
	                            LenseTypeDefinition type = reader.readNative(is);
	                            if (!type.isPlataformSpecific()){
	                              	moduleRepo.registerType(type, type.getGenericParameters().size());
	                            }
	                        }
	                    } else if (file.getName().equals("module.properties")){
	                        try (java.io.InputStream is = jar.getInputStream(file)){
	                            Properties pp = new Properties();
	                            pp.load(is);
	                            moduleRepo.setVersion( Version.valueOf(pp.getProperty("module.version")));
	                            moduleRepo.setName( pp.getProperty("module.name"));
	                        }
	                    }
	                }
	                
				}
				moduleRepo.consolidate();
			}
			
			
			
		} catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
		TypeDefinition type = types.get(filter);
		
		if (type == null){
			for(ModuleTypeContents module : modules){
				Optional<TypeDefinition> opType = module.resolveType(filter);
				
				if (opType.isPresent()){
					types.put(filter, opType.get());
					return opType;
				}
			}
			return Optional.empty();
		}
		
		return Optional.of(type);
	}

	@Override
	public List<ModuleTypeContents> resolveModuleByName(String name) {
		return modules.stream().filter(m -> m.getName().equals(name)).collect(Collectors.toList());
	}

	@Override
	public Optional<ModuleTypeContents> resolveModuleByNameAndVersion(ModuleIdentifier identifier) {
		return modules.stream().filter(m -> m.getName().equals(identifier.getName()) && m.getVersion().equals(identifier.getVersion())).findAny();
	}



}
