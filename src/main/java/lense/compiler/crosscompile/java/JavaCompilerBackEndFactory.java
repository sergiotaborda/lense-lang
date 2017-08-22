package lense.compiler.crosscompile.java;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;

public class JavaCompilerBackEndFactory implements CompilerBackEndFactory {


    DiagnosticListenerImpl diagnostic = new DiagnosticListenerImpl();
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private File base;

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
        public boolean compile(File file) throws IOException{
            if (file == null){
                return false;
            }

            return compile(Collections.singletonList(file));

        }

        public boolean compile(List<File> files) throws IOException{
            if (files.isEmpty()){
                return false;
            }

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostic, null, null);
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);

            List<File> classPath = new ArrayList<>(2);
            if (base != null){
                for (File jar : base.listFiles(f -> f.getName().endsWith(".jar"))){
                    classPath.add(jar);
                }
            }
            classPath.add(fileLocations.getTargetFolder());
            fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);

            return compiler.getTask(new PrintWriter(System.err), fileManager, diagnostic,  null /* Arrays.asList("-verbose")*/, null, compilationUnits).call();

            //file.delete();

        }
    }

    @Override
    public void setClasspath(File base) {
        this.base = base;
    }
    
    
    private class DiagnosticListenerImpl implements DiagnosticListener<JavaFileObject>{

        @Override
        public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
            if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)){
                throw new RuntimeException(diagnostic.getMessage(Locale.getDefault()));
            } else {
                System.out.println(diagnostic.getMessage(Locale.getDefault()));
            }
        }
        
    }

}
