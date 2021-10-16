package lense.compiler.crosscompile.javascript;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;

public class LenseToJsCompiler extends LenseCompiler{

    public LenseToJsCompiler(ModulesRepository globalRepository) {
        super("js", globalRepository, new JsCompilerBackEndFactory());
    }

    @Override
    protected void createModuleArchive(FileLocations locations, ModuleNode module, Set<String> applications)
            throws IOException, FileNotFoundException {
        // no-to
        // TODO pack with a web packer like commons-js
    }

	@Override
	protected boolean shouldGraphContain(DependencyRelationship parameter) {
		return DependencyRelationship.Structural == parameter;
	}
	
    @Override
    protected void initCorePhase(CompositePhase corePhase, Map<String, SourceFile> nativeTypes, UpdatableTypeRepository typeContainer) {
        DesugarPhase desugarProperties = new DesugarPhase(this.getCompilerListener());
        desugarProperties.setInnerPropertyPrefix("_");
        
        ErasurePhase erasurePhase = new ErasurePhase(this.getCompilerListener());
        
        corePhase.add(desugarProperties).add(erasurePhase);
        
    }

    @Override
    protected void collectNative(FileLocations fileLocations, Map<String, SourceFile> nativeTypes) throws IOException {
        // no-op for now
    }

	@Override
	protected SourceFile resolveNativeFile(SourceFolder folder, String name) {
		return folder.file( name + ".js");
	}

	@Override
	protected List<TypeDefinition> extactTypeDefinitionFromNativeType(UpdatableTypeRepository currentTypeRepository,
			Collection<SourceFile> files) throws IOException {

		return List.of();
	}

}
