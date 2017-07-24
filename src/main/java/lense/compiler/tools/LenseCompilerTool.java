package lense.compiler.tools;

import java.io.File;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import lense.compiler.Arguments;
import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.crosscompile.javascript.LenseToJsCompiler;
import lense.compiler.repository.ClasspathRepository;

public class LenseCompilerTool implements LenseTool{

    @Override
    public void run(Arguments arguments) {
        println("Runing from " + new File(".").getAbsoluteFile());
        
        File base;
        if (arguments.getRepositoryBase().isPresent()){
            base = new File(arguments.getRepositoryBase().get());
            if (!base.isAbsolute()){
                base = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getRepositoryBase().get());
            }
            
        } else {
            base = new File(System.getProperty("user.home"), ".lense/repository");
        }
      
        ClasspathRepository repo = new ClasspathRepository(base);

        println("Using repository at " + base);
        
        File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getSource());
        
        println("Compiling at " + moduleproject);
        
        long time = System.currentTimeMillis();
        
        
        LenseCompiler compiler;

        switch (arguments.getMode().orElse(LenseCommand.Mode.JAVA)){
        case JAVA:
            compiler  = new LenseToJavaCompiler(repo);
            break;
        case JAVA_SCRIPT:
            compiler  = new LenseToJsCompiler(repo);
            break;
        default:
            throw new ToolException("This target language is not supported yet");
        }

        compiler.setCompilerListener(new CompilerListener() {
            
            @Override
            public void warn(CompilerMessage error) {
                System.out.println("[WARN ]:" + error.getMessage());
            }
            
            @Override
            public void start() {}
            
            @Override
            public void error(CompilerMessage error) {
                System.out.println("[ERROR]:" + error.getMessage());
            }
            
            @Override
            public void end() {}

            @Override
            public void trace(CompilerMessage error) {
               //no-op
            }
        });
        compiler.compileModuleFromDirectory(moduleproject);
        
        long elapsed = System.currentTimeMillis() - time;
        
        println("Compilation ended ( " + elapsed + " ms)");
    }

    private static void println(String text) {
        System.out.println(text);
    }

}
