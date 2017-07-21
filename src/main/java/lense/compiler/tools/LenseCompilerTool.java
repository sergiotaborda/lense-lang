package lense.compiler.tools;

import java.io.File;

import lense.compiler.Arguments;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.repository.ClasspathRepository;

public class LenseCompilerTool implements LenseTool{

    @Override
    public void run(Arguments arguments) {

        File base = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getRepositoryBase());
        ClasspathRepository repo = new ClasspathRepository(base);

        println("Using repository at " + base);
        
        File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getSource());
        
        println("Compiling at " + moduleproject);
        
        long time = System.currentTimeMillis();
        
        final LenseToJavaCompiler compiler = new LenseToJavaCompiler(repo);
        compiler.compileModuleFromDirectory(moduleproject);
        
        long elapsed = System.currentTimeMillis() - time;
        
        println("Compilation ended ( " + elapsed + " ms)");
    }

    private static void println(String text) {
        System.out.println(text);
    }

}
