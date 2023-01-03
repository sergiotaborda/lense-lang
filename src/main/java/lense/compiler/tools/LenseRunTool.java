package lense.compiler.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import compiler.filesystem.DiskSourceFileSystem;
import lense.compiler.Arguments;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.repository.ClasspathModulesRepository;
import lense.compiler.repository.ComposedModulesRepository;
import lense.compiler.utils.Strings;

public class LenseRunTool implements LenseTool{

    @Override
    public void run(Arguments arguments) {
    	 println("Base folder is " + new File(".").getAbsoluteFile());
         
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
       
        
         File moduleProject = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getSource());
         
         var jars = moduleProject.listFiles(f -> f.getName().endsWith(".jar"));
         
         if (jars == null || jars.length ==0) {
        	 jars = new File(moduleProject, "compilation/modules").listFiles(f -> f.getName().endsWith(".jar"));
         }
         
         var all =  repo.getClassPath();
         
         
         var urls = new LinkedList<URL>();
         
         for (var sf : repo.getClassPath()) {
        	 for (var j : new File(sf.getPath().toString()).listFiles(f -> f.getName().endsWith(".jar"))) {
        		 try {
					urls.add(j.toURI().toURL());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	 }
         }
         
         try {
        	 urls.add(jars[0].toURI().toURL());
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
         
         println("Running " + moduleProject);

         
         String mainClassName = null;
         try (var jar = new JarFile(jars[0])){
			Manifest m = jar.getManifest();
			
			mainClassName = m.getMainAttributes().getValue("Main-Class");
			
         } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
         
        try {
        	
        	var urlArray = urls.toArray(new URL[urls.size()]);
			var childClassLoader = new URLClassLoader(urlArray);

			Thread.currentThread().setContextClassLoader(childClassLoader);  
			
			
			var mainClass = Class.forName(mainClassName, true , childClassLoader);
			
			
			mainClass.getMethod("main", String[].class).invoke(null, new Object[] { new String[0]});
			
		}catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private static void println(String text) {
        System.out.println(text);
    }

}
