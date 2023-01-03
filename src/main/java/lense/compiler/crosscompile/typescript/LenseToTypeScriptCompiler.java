package lense.compiler.crosscompile.typescript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import compiler.filesystem.SourceWalker;
import compiler.filesystem.SourceWalkerResult;
import lense.compiler.CompilationError;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.NativeSourceInfo;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;

public class LenseToTypeScriptCompiler extends LenseCompiler{

    public LenseToTypeScriptCompiler(ModulesRepository globalRepository) {
        super("ts", globalRepository, new TsCompilerBackEndFactory());
    }


    @Override
    protected void initCorePhase(CompositePhase corePhase,  Map<String, NativeSourceInfo> nativeTypes, UpdatableTypeRepository typeContainer) {
        DesugarPhase desugarProperties = new DesugarPhase(this.getCompilerListener());
        desugarProperties.setInnerPropertyPrefix("_");
        
        ErasurePhase erasurePhase = new ErasurePhase(this.getCompilerListener());
        
        corePhase.add(desugarProperties).add(erasurePhase);
        
    }

    @Override
    protected void collectNative(FileLocations fileLocations, Map<String, NativeSourceInfo> nativeTypes) throws IOException {
    	
		if (!fileLocations.getNativeFolder().exists()){
			return;
		}

		List<SourceFile> tsFiles = new LinkedList<>();
		Map<SourceFile, SourceFile> lenseFiles = new HashMap<>();

		var rootDir = fileLocations.getNativeFolder();
	    
		rootDir.walkTree(new SourceWalker(){

			@Override
			public SourceWalkerResult visitFile(SourceFile file) {
				if (file.getName().endsWith(".ts")){
					tsFiles.add(file);
					var relativePath = fileLocations.getNativeFolder().getPath().relativize(file.getPath());
					
					var sourcePathName = relativePath.toString().replace(".ts", ".lense").replace('>', File.separatorChar);
					var source = fileLocations.getSourceFolder().file(sourcePathName);
					if (source.exists()) {
						lenseFiles.put(file, source);
					}
					
				}
				return SourceWalkerResult.CONTINUE;
			}

		});

		if(tsFiles.isEmpty()) {
			return;
		}
		
		
		// compile all files
		outter: for (var n : tsFiles){
			
		
			var name = n.getName().substring(0,  n.getName().length() - 5);
			var source = resolveNativeFile(n.parentFolder(), name);

			try (var reader = new BufferedReader(new InputStreamReader(n.inputStream()))){
				if (!name.equals("Placeholder") && reader.lines().anyMatch(line -> line.startsWith("@Placeholder"))) {
					continue outter;
				}
			}
			
			var packagePath = rootDir.getPath().relativize(n.getPath()).getParent();
			var packageFolder = fileLocations.getTargetFolder().folder(packagePath);
			
			var target =  packageFolder.file(name  + ".ts");

			target.parentFolder().ensureExists();

			if (source.isPresent() && !source.get().exists()){
				throw new CompilationError("Compiled file with TS compiler does not exist (" + source.toString() +"). ");
			} else {
				source.get().moveTo(target);
				
			
				var lense = lenseFiles.get(n);
				nativeTypes.put(packagePath.join(".") + "." + name, new NativeSourceInfo(target, lense));

			}

		}

    }
	
    @Override
	protected List<TypeDefinition> extactTypeDefinitionFromNativeType(UpdatableTypeRepository currentTypeRepository,
			Collection<NativeSourceInfo> nativeFiles) throws IOException {

		
	    var nativeTypesDefs = new LinkedList<TypeDefinition>();
	 
		return nativeTypesDefs;
	}

	@Override
	protected void createModuleArchive(FileLocations locations, ModuleNode module, Set<String> applications)
			throws IOException, FileNotFoundException {
		// TODO Auto-generated method stub
		
	}





	@Override
	protected Optional<SourceFile> resolveNativeFile(SourceFolder folder, String name) {
		return Optional.of(folder.file(name + ".ts"));
	}



	@Override
	protected boolean shouldGraphContain(DependencyRelationship parameter) {
		return DependencyRelationship.Structural == parameter;
	}
}
