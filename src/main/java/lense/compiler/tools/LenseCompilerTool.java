package lense.compiler.tools;

import java.io.File;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.filesystem.DiskSourceFileSystem;
import lense.compiler.Arguments;
import lense.compiler.LenseCompiler;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.crosscompile.javascript.LenseToJsCompiler;
import lense.compiler.crosscompile.typescript.LenseToTypeScriptCompiler;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.repository.ClasspathModulesRepository;
import lense.compiler.repository.ComposedModulesRepository;
import lense.compiler.utils.Strings;

public class LenseCompilerTool implements LenseTool{

    @Override
    public void run(Arguments arguments) {
        println("Base from " + new File(".").getAbsoluteFile());
        
        ModulesRepository repo;

        
        if (arguments.getRepositoryBase().isPresent()){
        	
        	var bases = Strings.split(arguments.getRepositoryBase().get(), ",");
        	
        	if(bases.length == 1) {
        		
        		 var base = new File(bases[0]);
                 if (!base.isAbsolute()){
                     base = new File(new File(".").getAbsoluteFile().getParentFile(), bases[0]);
                 }
                 
                 if (!base.exists()){
                   	 println("No repository found at " + base);
                        return;
                 }
                    
                 println("Using repository at " + base);
                 
                 repo = new ClasspathModulesRepository(DiskSourceFileSystem.instance().folder(base));
                 
        	} else {
        		ComposedModulesRepository composedRepo = new ComposedModulesRepository();
        		
        		for (var basePath : bases) {
        			var base = new File(basePath);
        			if (!base.isAbsolute()){
                        base = new File(new File(".").getAbsoluteFile().getParentFile(),basePath);
                    }
                    
                    if (!base.exists()){
                      	 println("No repository found at " + base);
                           return;
                    }
                       
                    println("Using repository at " + base);
                    
                    composedRepo.addRepositiory(new ClasspathModulesRepository(DiskSourceFileSystem.instance().folder(base)));
        		}
        		
        		repo = composedRepo;
        	}
           
            
        } else {
            var base = new File(System.getProperty("user.home"), ".lense/repository");
            
            if (!base.exists()){
           	 	println("No repository found at " + base + ". Creating one");
                base.mkdirs();
            }
            
            println("Using repository at " + base);
            
            repo = new ClasspathModulesRepository(DiskSourceFileSystem.instance().folder(base));
        }
      
       
        File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getSource());
        
        println("Compiling at " + moduleproject);
        

        LenseCompiler compiler = switch (arguments.getMode().orElse(LenseCommand.Mode.JAVA)) {
	        case JAVA -> new LenseToJavaCompiler(repo);
	        case JAVA_SCRIPT -> new LenseToJsCompiler(repo);
	        case TYPE_SCRIPT -> new LenseToTypeScriptCompiler(repo);
	        default -> throw new ToolException("This target language is not supported yet");
        };

        compiler.setCompilerListener(new CompilerListener() {
            
            @Override
            public void warn(CompilerMessage error) {
                println("[WARN ]:" + error.getMessage());
            }
            
            @Override
            public void start() {}
            
            @Override
            public void error(CompilerMessage error) {
                println("[ERROR]:" + error.getMessage());
            }
            
            @Override
            public void end() {}

            @Override
            public void trace(CompilerMessage error) {
            	println("[TRACE]:" + error.getMessage());
            }
        });
        
        long time = System.currentTimeMillis();
        
        compiler.compileModuleFromDirectory(DiskSourceFileSystem.instance().folder(moduleproject));
        
        long elapsed = System.currentTimeMillis() - time;
        
        println("Compilation ended ( " + elapsed + " ms)");
    }

    private static void println(String text) {
        System.out.println(text);
    }

}
