package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.DiskSourceFileSystem;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaCompilerBackEndFactory implements CompilerBackEndFactory {


    DiagnosticListenerImpl diagnostic = new DiagnosticListenerImpl();
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private List<SourceFolder> classpath;

    public JavaCompilerBackEndFactory (){}

    @Override
    public JavaCompilerBackEnd create(FileLocations fileLocations) {
        return new JavaCompilerBackEnd(fileLocations);
    }

    public class JavaCompilerBackEnd implements CompilerBackEnd {

    	
        private FileLocations fileLocations;
        private OutToJavaSource source;

        public JavaCompilerBackEnd(FileLocations fileLocations) {
            this.fileLocations= fileLocations;
            this.source = new OutToJavaSource(fileLocations);
        }

        @Override
        public void use(CompiledUnit unit) {
        	
            try{
            	
            	
                compile(source.toSource(unit));
                
            } catch (Exception e){
                throw new RuntimeException("Error compiling unit " + unit.getUnit().getName(),e);
            }
        }
        public boolean compile(SourceFile file) throws IOException{
            if (file == null){
                return false;
            }

            return compile(Collections.singletonList(file));

        }

        public boolean compile(List<SourceFile> files) throws IOException{
            if (files.isEmpty()){
                return false;
            }
            
            var fileSystem =  DiskSourceFileSystem.instance();
        	
            var localFiles = fileSystem.convertToFiles(files);
            var target = fileSystem.convertToFile(fileLocations.getTargetFolder());
         
            
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostic, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(localFiles);

            List<File> classPath = new ArrayList<>(2);
            if (classpath != null){
            	 for (var base : classpath){
            		 for (var jar : base.children(f -> f.getName().endsWith(".jar"))){
                     	var jarFile = fileSystem.convertToFile(jar);
                         classPath.add(jarFile);
                     }
                 }
            }
            classPath.add(target);
            fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);

            return compiler.getTask(new PrintWriter(System.err), fileManager, diagnostic,  null /* Arrays.asList("-verbose")*/, null, compilationUnits).call();

            //file.delete();

        }
    }

	@Override
	public void setClasspath(List<SourceFolder> classpath) {
		this.classpath =classpath;
	}
    
    
    private class DiagnosticListenerImpl implements DiagnosticListener<JavaFileObject>{

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)){
                throw new RuntimeException(diagnostic.toString());
            } else {
                System.out.println(diagnostic.toString());
            }
        }
        
    }

}
