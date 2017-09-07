package lense.compiler.crosscompile.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.ast.ModuleNode;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.repository.UpdatableTypeRepository;

public class LenseToJsCompiler extends LenseCompiler{

    public LenseToJsCompiler(ModulesRepository globalRepository) {
        super("js", globalRepository, new JsCompilerBackEndFactory());
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
    protected void compileNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException {
        // no-op for now
    }

}
