package lense.compiler.crosscompile.typescript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lense.compiler.CompilationError;
import lense.compiler.FileLocations;
import lense.compiler.FundamentalTypesModuleContents;
import lense.compiler.LenseCompiler;
import lense.compiler.asm.LoadedLenseTypeDefinition;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.crosscompile.javascript.JsCompilerBackEndFactory;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;

public class LenseToTypeScriptCompiler extends LenseCompiler{

    public LenseToTypeScriptCompiler(ModulesRepository globalRepository) {
        super("ts", globalRepository, new JsCompilerBackEndFactory());
    }

    @Override
    protected void createModuleArchive(FileLocations locations, ModuleNode module, File base, Set<String> applications)
            throws IOException, FileNotFoundException {
        // no-to
        // TODO pack with a web packer like commons-js
    }

    @Override
    protected void initCorePhase(CompositePhase corePhase, Map<String, File> nativeTypes, UpdatableTypeRepository typeContainer) {
        DesugarPhase desugarProperties = new DesugarPhase(this.getCompilerListener());
        desugarProperties.setInnerPropertyPrefix("_");
        
        ErasurePhase erasurePhase = new ErasurePhase(this.getCompilerListener());
        
        corePhase.add(desugarProperties).add(erasurePhase);
        
    }

    @Override
    protected void collectNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException {
		if (!fileLocations.getNativeFolder().exists()){
			return;
		}

		List<File> files = new LinkedList<>();

		final Path rootDir = fileLocations.getNativeFolder().toPath();


		Files.walkFileTree(rootDir, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes atts) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes mainAtts)
					throws IOException {

				if (path.toString().endsWith(".ts")){
					files.add(path.toFile());
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path path,
					IOException exc) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path path, IOException exc)
					throws IOException {
				exc.printStackTrace();

				return path.equals(rootDir)? FileVisitResult.TERMINATE:FileVisitResult.CONTINUE;
			}
		});
    
		for (File n : files){
			String packageFile = n.getAbsolutePath().substring(rootDir.toString().length());
		
			var name = n.getName().substring(0,  n.getName().length() - 3);
			
			File source = resolveNativeFile(n.getParentFile(), name);



			File target = new File(fileLocations.getTargetFolder(),packageFile);

			target.getParentFile().mkdirs();

		
			Files.copy(source.toPath(), target.toPath());
				
			nativeTypes.put(name, target);


		}
    }

	@Override
	protected File resolveNativeFile(File folder, String name) {
		return  new File( folder, name + ".ts");
	}

	@Override
	protected List<TypeDefinition> extactTypeDefinitionFronNativeType(
			UpdatableTypeRepository currentModuleRepository,
			Collection<File> nativeFiles
	) throws IOException {
		
	    var nativeTypesDefs = new LinkedList<TypeDefinition>();
	    var fundamentalTypesModuleContents = new FundamentalTypesModuleContents();
	      
	    // TODO read from defenition metadata
		for( File target : nativeFiles) {
			
			var name = target.getName().substring(0, target.getName().length() - 3);
			
	
			var types = fundamentalTypesModuleContents.resolveTypesMap(name);
			
			TypeDefinition type;
			if (types.size() == 1) {
				type = types.values().iterator().next();
			} else {
				LenseUnitKind kind = name.equals(name.toLowerCase())  ?  LenseUnitKind.Object : LenseUnitKind.Unkown;
				
	  			type =  new LoadedLenseTypeDefinition(name, kind, null);
			}

      		currentModuleRepository.registerType(type, type.getGenericParameters().size());
      		
      		nativeTypesDefs.add(type);
  		}
		
		return nativeTypesDefs;
	}
}
