package lense.compiler.crosscompile.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.ast.ModuleNode;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPropertiesPhase;
import lense.compiler.repository.TypeRepository;

public class LenseToJsCompiler extends LenseCompiler{

    public LenseToJsCompiler(TypeRepository globalRepository) {
        super("js", globalRepository, new JsCompilerBackEndFactory());
    }

    @Override
    protected void createModuleArchive(FileLocations locations, ModuleNode module, File base, Set<String> applications)
            throws IOException, FileNotFoundException {
        // no-to
        // TODO pack with a web packer like commons-js
    }

    @Override
    protected void initCorePhase(CompositePhase corePhase, Map<String, File> nativeTypes) {
        DesugarPropertiesPhase desugarProperties = new DesugarPropertiesPhase(this.getCompilerListener());
        desugarProperties.setInnerPropertyPrefix("_");
        corePhase.add(desugarProperties);
        
    }

    @Override
    protected void compileNative(FileLocations fileLocations, Map<String, File> nativeTypes) throws IOException {
        // no-op for now
    }

}
