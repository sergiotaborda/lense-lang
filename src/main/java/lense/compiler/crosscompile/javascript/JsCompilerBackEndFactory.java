package lense.compiler.crosscompile.javascript;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.crosscompile.java.JavaSourceWriterVisitor;

public class JsCompilerBackEndFactory implements CompilerBackEndFactory {

    @Override
    public CompilerBackEnd create(FileLocations locations) {
        return new JsCompilerBackEnd(locations);
    }

    @Override
    public void setClasspath(File base) {
        // TODO Auto-generated method stub
        
    }
    
    static class JsCompilerBackEnd implements CompilerBackEnd {

        private FileLocations locations;

        public JsCompilerBackEnd(FileLocations locations) {
            this.locations = locations;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void use(CompiledUnit unit) {
            toSource(unit);
        }

        public File toSource(CompiledUnit unit) {
            
            File target = locations.getTargetFolder();
            File compiled = target;
            if (target.isDirectory()){
                AstNode node = unit.getAstRootNode().getChildren().get(0);
                
                if (!(node instanceof ClassTypeNode)){
                    return null;
                }
                ClassTypeNode t = (ClassTypeNode)node;
                
                if (t.isNative()){
                    return null;
                }
                
                String path = t.getName().replace('.', '/');
                int pos = path.lastIndexOf('/');
                String filename = path.substring(pos+1) + ".js";
                File folder;
                if (pos >=0){
                    path = path.substring(0, pos);
                    folder = new File(target, path );
                } else {
                    folder = target;
                }
                
                folder.mkdirs();
                
                compiled = new File(folder, filename);
                try {
                    compiled.createNewFile();
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }

            try(PrintWriter writer = new PrintWriter(new FileWriter(compiled))){
                TreeTransverser.transverse(unit.getAstRootNode(), new JavascriptSourceWriterVisitor(writer));
                return compiled;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        
    }
    
}
